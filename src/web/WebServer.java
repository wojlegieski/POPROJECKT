package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import database.DatabaseManager;
import database.GameResult;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WebServer {
    private final DatabaseManager dbManager;
    private final ObjectMapper objectMapper;
    private Server server;
    
    public WebServer(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.objectMapper = new ObjectMapper();
    }
    
    public void start() throws Exception {
        server = new Server(8080);
        
        // Kontekst dla API
        ServletContextHandler apiContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        apiContext.setContextPath("/api");
        
        // Endpointy API
        apiContext.addServlet(new ServletHolder(new LoginServlet()), "/login");
        apiContext.addServlet(new ServletHolder(new RegisterServlet()), "/register");
        apiContext.addServlet(new ServletHolder(new ResultsServlet()), "/results");
        apiContext.addServlet(new ServletHolder(new SaveResultServlet()), "/save-result");
        apiContext.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
        
        server.setHandler(apiContext);
        
        server.start();
        System.out.println("Web server started on http://localhost:8080");
        System.out.println("API endpoints available at http://localhost:8080/api/");
    }
    
    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
    
    // Servlet do logowania
    private class LoginServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
                throws ServletException, IOException {
            resp.setContentType("application/json");
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
            
            if (req.getMethod().equals("OPTIONS")) {
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            try {
                Map<String, String> requestData = objectMapper.readValue(req.getReader(), Map.class);
                String username = requestData.get("username");
                String password = requestData.get("password");
                
                // Hashowanie hasła
                String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
                
                Integer userId = dbManager.authenticateUser(username, passwordHash);
                
                if (userId != null) {
                    HttpSession session = req.getSession();
                    session.setAttribute("userId", userId);
                    session.setAttribute("username", username);
                    
                    resp.getWriter().write("{\"success\": true, \"message\": \"Logged in successfully\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write("{\"success\": false, \"message\": \"Invalid credentials\"}");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\": false, \"message\": \"Invalid request\"}");
            }
        }
    }
    
    // Servlet do rejestracji
    private class RegisterServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
                throws ServletException, IOException {
            resp.setContentType("application/json");
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
            
            if (req.getMethod().equals("OPTIONS")) {
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            try {
                Map<String, String> requestData = objectMapper.readValue(req.getReader(), Map.class);
                String username = requestData.get("username");
                String password = requestData.get("password");
                
                String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
                
                boolean success = dbManager.registerUser(username, passwordHash);
                
                if (success) {
                    resp.getWriter().write("{\"success\": true, \"message\": \"User registered successfully\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write("{\"success\": false, \"message\": \"Username already exists\"}");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\": false, \"message\": \"Invalid request\"}");
            }
        }
    }
    
    // Servlet do pobierania wyników
    private class ResultsServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
                throws ServletException, IOException {
            resp.setContentType("application/json");
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
            
            if (req.getMethod().equals("OPTIONS")) {
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"success\": false, \"message\": \"Not logged in\"}");
                return;
            }
            
            Integer userId = (Integer) session.getAttribute("userId");
            List<GameResult> results = dbManager.getUserResults(userId);
            
            resp.getWriter().write(objectMapper.writeValueAsString(results));
        }
    }
    
    // Servlet do zapisywania wyników
    private class SaveResultServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
                throws ServletException, IOException {
            resp.setContentType("application/json");
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
            
            if (req.getMethod().equals("OPTIONS")) {
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"success\": false, \"message\": \"Not logged in\"}");
                return;
            }
            
            try {
                Map<String, Object> requestData = objectMapper.readValue(req.getReader(), Map.class);
                Integer userId = (Integer) session.getAttribute("userId");
                Double timeSeconds = (Double) requestData.get("timeSeconds");
                Integer lapCount = (Integer) requestData.get("lapCount");
                
                boolean success = dbManager.saveResult(userId, timeSeconds, lapCount);
                
                if (success) {
                    resp.getWriter().write("{\"success\": true, \"message\": \"Result saved successfully\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"success\": false, \"message\": \"Failed to save result\"}");
                }
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\": false, \"message\": \"Invalid request\"}");
            }
        }
    }
    
    // Servlet do wylogowania
    private class LogoutServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
                throws ServletException, IOException {
            resp.setContentType("application/json");
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
            
            if (req.getMethod().equals("OPTIONS")) {
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            resp.getWriter().write("{\"success\": true, \"message\": \"Logged out successfully\"}");
        }
    }
} 