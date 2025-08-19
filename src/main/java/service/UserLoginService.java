package service;

import com.google.gson.JsonObject;
import dao.UserDAO;
import dao.UserLoginDAO;
import java.security.MessageDigest;
import java.util.Base64;
import model.User;

public class UserLoginService {

    private final UserLoginDAO userLoginDAO = new UserLoginDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Hashes the input password using SHA-256 and encodes in Base64.
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8")); // explicit UTF-8
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates login credentials and checks if password change is needed.
     */
    public JsonObject validateUser(String username, String password) {
    JsonObject jsonResponse = new JsonObject();
    String[] dbData = userLoginDAO.getPasswordDetailsByUsername(username);
System.out.print(username);
    if (dbData != null) {
        String storedHashedPassword = dbData[0];
        int userID = Integer.parseInt(dbData[1]);
        boolean passwordChanged = Boolean.parseBoolean(dbData[2]);
        
        // Fix 1: Trim the password before hashing
        String trimmedPassword = password.trim(); 
        String hashedPasswordInput = hashPassword(trimmedPassword);

        // DEBUG: print hashes
        System.out.println("Username: " + username);
        System.out.println(">>> Input hash:    " + hashedPasswordInput);
        System.out.println(">>> Stored hash:   " + storedHashedPassword);
        System.out.println(">>> Stored length: " + storedHashedPassword.length());
        System.out.println(">>> Input length:  " + hashedPasswordInput.length());

        if (hashedPasswordInput != null && hashedPasswordInput.equals(storedHashedPassword)) {
            if (!passwordChanged) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Initial login detected. Please change your password.");
                jsonResponse.addProperty("passwordChanged", false);
                jsonResponse.addProperty("userID", userID);
            } else {
                User user = userDAO.getUserDetails(userID);
                if (user != null) {
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Login successful.");
                    jsonResponse.addProperty("firstName", user.getFirstName());
                    jsonResponse.addProperty("roleID", user.getRoleID());
                    jsonResponse.addProperty("passwordChanged", true);
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "User details not found.");
                }
            }
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid username or password.");
        }
    } else {
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", "Invalid username or password.");
    }

    return jsonResponse;
}
    /**
     * Handles password change request.
     */
    public JsonObject changePassword(int userID, String newPassword) {
        JsonObject jsonResponse = new JsonObject();
        String newHashedPassword = hashPassword(newPassword);

        if (newHashedPassword != null && userLoginDAO.updatePassword(userID, newHashedPassword)) {
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Password changed successfully.");
        } else {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Failed to change password. Please try again.");
        }

        return jsonResponse;
    }
}
