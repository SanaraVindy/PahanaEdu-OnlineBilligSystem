package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.UserDAO;
import dao.UserLoginDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import service.UserLoginService;

@WebServlet("/api/change-password")
public class PasswordChangeController extends HttpServlet {

    private final Gson gson = new Gson();
    private UserLoginService userLoginService;

    /**
     * This is the no-argument constructor required by the Servlet container.
     * It initializes the UserLoginService with concrete DAO implementations.
     */
    public PasswordChangeController() {
        this.userLoginService = new UserLoginService(new UserLoginDAO(), new UserDAO());
    }

    /**
     * This constructor is used for unit testing. It allows us to inject
     * mock or fake service instances.
     * @param userLoginService The service to be injected.
     */
    public PasswordChangeController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject responseJson = new JsonObject();

        try {
            JsonObject requestJson = gson.fromJson(request.getReader(), JsonObject.class);
            int userID = requestJson.get("userID").getAsInt();
            String newPassword = requestJson.get("newPassword").getAsString();

            if (newPassword == null || newPassword.trim().isEmpty()) {
                responseJson.addProperty("success", false);
                responseJson.addProperty("message", "New password cannot be empty.");
            } else {
                responseJson = userLoginService.changePassword(userID, newPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseJson.addProperty("success", false);
            responseJson.addProperty("message", "Server error during password change.");
        }

        response.getWriter().write(responseJson.toString());
    }
}
