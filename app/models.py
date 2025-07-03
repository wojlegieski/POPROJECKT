from datetime import datetime
import sqlalchemy as sa
from sqlalchemy.orm import mapped_column, relationship
from flask_security import UserMixin, RoleMixin
from .app import db

# Here you can read about useful column types (Integer, String, DateTime, etc...):
# https://docs.sqlalchemy.org/en/20/core/type_basics.html#generic-camelcase-types

# Here you can read about relationships between models:
# https://docs.sqlalchemy.org/en/20/orm/basic_relationships.html
# https://stackoverflow.com/questions/3113885/difference-between-one-to-many-many-to-one-and-many-to-many

# Here you can read about using models defined below to work
# with the database (creating rows, selecting rows, deleting rows, etc...):
# https://flask-sqlalchemy.palletsprojects.com/en/3.1.x/queries/


# RolesUsers is a predefined model required by the Flask-Security-Too library
# You can find it at https://flask-security-too.readthedocs.io/en/stable/quickstart.html#sqlalchemy-application-w-o-flask-sqlalchemy

# Role is a predefined model required by the Flask-Security-Too library
# You can find it at https://flask-security-too.readthedocs.io/en/stable/quickstart.html#sqlalchemy-application-w-o-flask-sqlalchemy
# User is a predefined model required by the Flask-Security-Too library
# You can find it at https://flask-security-too.readthedocs.io/en/stable/quickstart.html#sqlalchemy-application-w-o-flask-sqlalchemy


class RolesUsers(db.Model):
    __tablename__ = 'roles_users'
    id      = mapped_column(sa.Integer(), primary_key=True)
    user_id = mapped_column(sa.Integer(), sa.ForeignKey('user.id'))
    role_id = mapped_column(sa.Integer(), sa.ForeignKey('role.id'))
    __table_args__ = {'extend_existing': True}



class Role(db.Model, RoleMixin):
    __tablename__ = 'role'
    id          = mapped_column(sa.Integer(), primary_key=True)
    name        = mapped_column(sa.String(80), unique=True)
    description = mapped_column(sa.String(255))
    users       = relationship('User', secondary='roles_users', back_populates="roles", lazy=True)


class User(db.Model, UserMixin):
    __tablename__ = 'user'
    id                = mapped_column(sa.Integer(), primary_key=True)
    username          = mapped_column(sa.String(255), unique=True, nullable=True)
    password          = mapped_column(sa.String(255), nullable=False)
    active            = mapped_column(sa.Boolean())
    fs_uniquifier     = mapped_column(sa.String(255), unique=True, nullable=False)
    roles         = relationship('Role', secondary='roles_users', back_populates="users", lazy=True)
    timetables = relationship("Timetable", back_populates="user", lazy=True)

class Timetable(db.Model):
    tablename = 'timetable'
    id = mapped_column(sa.Integer, primary_key=True)
    user_id = mapped_column(sa.Integer, sa.ForeignKey('user.id'), nullable=False)
    duration = mapped_column(sa.Float) 
    count = mapped_column(sa.Integer)
    user = relationship("User", back_populates="timetables")

class Result(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"), nullable=False)
    time_seconds = db.Column(db.Float, nullable=False)
    lap_count = db.Column(db.Integer, nullable=False)
    completed_at = db.Column(db.DateTime, default=datetime.utcnow)
