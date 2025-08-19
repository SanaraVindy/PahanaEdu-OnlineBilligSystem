package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import service.UserLoginService;



@WebServlet("/api/login")
public class UserLoginController extends HttpServlet {

    private final UserLoginService userLoginService = new UserLoginService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject responseJson = new JsonObject();
        
        try {
            // Read JSON payload from the request body
            JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
            String username = requestJson.get("username").getAsString();
            String password = requestJson.get("password").getAsString();

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                responseJson.addProperty("success", false);
                responseJson.addProperty("message", "Username and password are required.");
            } else {
                // Call the service to validate credentials
                responseJson = userLoginService.validateUser(username, password);
            }

        } catch (Exception e) {
            e.printStackTrace();
            responseJson.addProperty("success", false);
            responseJson.addProperty("message", "Server error during login processing.");
        }
        
        // Send the JSON response back to the frontend
        response.getWriter().write(responseJson.toString());
    }
}