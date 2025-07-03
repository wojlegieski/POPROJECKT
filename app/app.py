import os
import sys
from flask import Flask, render_template, request, jsonify, make_response
from werkzeug.debug import DebuggedApplication
from flask_sqlalchemy import SQLAlchemy
from flask_security import Security, SQLAlchemyUserDatastore, hash_password, auth_required
from flask_security.models import fsqla_v2 as fsqla
from flask_babel import Babel
from flask_login import login_user, current_user
import requests
import uuid

db = SQLAlchemy()
security = Security()


# Flask quickstart:
# https://flask.palletsprojects.com/en/3.0.x/quickstart/
# Flask factory pattern:
# https://flask.palletsprojects.com/en/3.0.x/tutorial/factory/

def create_app():
    
    # Static files (e.g. css, js, images) will be stored one level up, in the ~/public_html directory
    STATIC_FOLDER = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'public_html'))


    # Create and configure the app
    app = Flask(__name__,
                instance_relative_config=False,
                static_folder=STATIC_FOLDER,
                static_url_path='/static'
    )


    # Load config from file config.py
    app.config.from_pyfile('config.py')


    # Keep static files path in app config
    app.config["STATIC_FOLDER"] = STATIC_FOLDER


    # Enable debug mode - you will see beautiful error messages later :)
    # https://flask.palletsprojects.com/en/3.0.x/debugging/
    app.debug = True
    app.wsgi_app = DebuggedApplication(app.wsgi_app)


    # Ensure the instance folder exists - nothing interesting now
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass


    # Enable translations - useful in login/registration forms
    Babel(app)


    # https://flask-sqlalchemy.palletsprojects.com/en/3.1.x/quickstart/#configure-the-extension
    app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///database.sqlite"
    db.init_app(app)



    # Setup Flask-Security module (users, session, login, registration, etc)
    # https://flask-security-too.readthedocs.io/en/stable/quickstart.html#basic-sqlalchemy-application
    from .models import User, Role
    user_datastore = SQLAlchemyUserDatastore(db, User, Role)
    security.init_app(app, user_datastore)
    


    # Create test user and 50 blog entries if do not exist
    with app.app_context():
        db.create_all()
        if not user_datastore.find_user(username="test"):
            user = user_datastore.create_user(
                username="test",
                password=hash_password("test567")
            )
            db.session.commit()


    # Setup custom registration confirmation view (see config.py)
    @app.route('/registered')
    def registered():
        return render_template('security/registered.html')


    # Setup custom "Not found" page
    # https://flask.palletsprojects.com/en/3.0.x/errorhandling/#custom-error-pages
    @app.errorhandler(404)
    def page_not_found(e):
        return render_template('404.html'), 404
    
    
    def verify_recaptcha(response_token):
        secret = app.config.get("RECAPTCHA_PRIVATE_KEY")
        verify_url = "https://www.google.com/recaptcha/api/siteverify"
        payload = {"secret": secret, "response": response_token}
        response = requests.post(verify_url, data=payload)
        return response.json().get("success", False)
    

    @app.route("/login", methods=["POST"])
    def login():
        data = request.get_json()
        username = data.get("username")
        password = data.get("password")

        recaptcha_token = data.get("recaptcha")
        if not verify_recaptcha(recaptcha_token):
            return jsonify({"error": "Invalid reCAPTCHA"}), 400

        from flask_security.utils import verify_and_update_password
        user = user_datastore.find_user(username=username)

        if user and verify_and_update_password(password, user): 
            login_user(user)  # from flask_login
            return jsonify({"message": "Logged in", "user": user.username}), 200
        else:
            return jsonify({"error": "Invalid credentials"}), 401

    @app.route("/api/login", methods=["POST"])
    def api_login():
        data = request.get_json()
        username = data.get("username")
        password = data.get("password")
        from flask_security.utils import verify_and_update_password
        user = user_datastore.find_user(username=username)

        if user and verify_and_update_password(password, user): 
            login_user(user)  # from flask_login
            response = make_response(jsonify({"message": "Logged in", "user": user.username}), 200)
            return response
        else:
            return jsonify({"error": "Invalid credentials"}), 401

    @app.route("/register", methods=["POST"])
    def api_register():
        data = request.get_json()
        username = data.get("username")
        password = data.get("password")
        recaptcha_token = data.get("recaptcha")

        if not all([username, password, recaptcha_token]):
            return jsonify({"error": "Missing fields"}),
        if not verify_recaptcha(recaptcha_token):
            return jsonify({"error": "Invalid reCAPTCHA"}), 400

        if user_datastore.find_user(username=username):
            return jsonify({"error": "User already exists"}), 400

        user = user_datastore.create_user(
            username=username,
            password=hash_password(password)
        )
        db.session.commit()
        return jsonify({"message": "User registered"}), 200

    
    @app.after_request
    def allow_cors(response):
        import re
        http_origin = request.environ.get('HTTP_ORIGIN', None)
        http_access_ctrl_req_headers = request.environ.get(
            'HTTP_ACCESS_CONTROL_REQUEST_HEADERS',
            None
        )
        if http_origin and re.search(r'^[a-zA-Z0-9\-\_\/\:\.]+$', http_origin, re.DOTALL):
            response.headers['Access-Control-Allow-Origin'] = http_origin
            response.headers['Access-Control-Allow-Credentials'] = "true"
            response.headers['Access-Control-Allow-Methods'] = ("GET, POST, PUT, PATCH, DELETE, "
                                                                "OPTIONS")
            response.headers['Access-Control-Expose-Headers'] = ("*, Content-Disposition, "
                                                                 "Content-Length, "
                                                                 "X-Uncompressed-Content-Length")
            if http_access_ctrl_req_headers:
                response.headers['Access-Control-Allow-Headers'] = http_access_ctrl_req_headers

        return response 
        

    # @app.route("/results", methods=["GET"])
    # def get_results():
    #     from flask_login import current_user
    #     if not current_user.is_authenticated:
    #         return jsonify({"error": "Not authenticated"}), 401

    #     from .models import Result
    #     results = Result.query.filter_by(user_id=current_user.id).order_by(Result.completed_at).all()
    #     return jsonify([
    #         {
    #             "time_seconds": r.time_seconds,
    #             "lap_count": r.lap_count,
    #             "completed_at": r.completed_at.isoformat()
    #         } for r in results
    #     ])

    @app.route("/results")
    @auth_required("session")  # lub usuń jeśli nie chcesz zabezpieczać
    def show_results():
        from .models import Timetable
        results = Timetable.query.order_by(Timetable.id.desc()).all()
        return render_template("results.html", results=results)

    @app.route("/api/timetable", methods=["POST"])
    @auth_required("session")
    def add_timetable():
        data = request.get_json()
        duration = data.get("duration")
        count = data.get("count")

        from .models import Timetable

        if duration is None or count is None:
            return jsonify({"error": "Missing duration or count"}), 400

        entry = Timetable(
            user_id=current_user.id,
            duration=duration,
            count=count
        )
        db.session.add(entry)
        db.session.commit()

        return jsonify({"message": "Entry added"}), 200

    return app
