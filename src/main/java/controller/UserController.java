package controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.User;
import service.UserService;

@WebServlet("/api/users/*")
public class UserController extends HttpServlet {
    private static final Gson gson = new Gson();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String searchText = request.getParameter("search");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                // Handles GET /api/users/{id}
                int userID = Integer.parseInt(pathInfo.substring(1));
                User user = userService.getUserByID(userID);
                if (user != null) {
                    response.getWriter().write(gson.toJson(user));
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"User not found\"}");
                }
            } else if (searchText != null) {
                // Handles GET /api/users?search={text}
                List<User> users = userService.searchUsers(searchText);
                response.getWriter().write(gson.toJson(users));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                // Handles GET /api/users
                List<User> users = userService.getAllUsers();
                response.getWriter().write(gson.toJson(users));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid user ID format\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"An unexpected error occurred\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            User user = gson.fromJson(request.getReader(), User.class);
            boolean success = userService.addUser(user);
            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"message\":\"User added successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Failed to add user\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"An unexpected error occurred\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"User ID is required\"}");
                return;
            }
            int userID = Integer.parseInt(pathInfo.substring(1));
            User user = gson.fromJson(request.getReader(), User.class);
            user.setUserID(userID);
            
            boolean success = userService.updateUser(user);
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"User updated successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Failed to update user. It might not exist.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid user ID format\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"An unexpected error occurred\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"User ID is required\"}");
                return;
            }
            int userID = Integer.parseInt(pathInfo.substring(1));

            boolean success = userService.deleteUser(userID);
            if (success) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Failed to delete user. It might not exist.\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid user ID format\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"An unexpected error occurred\"}");
            e.printStackTrace();
        }
    }
}