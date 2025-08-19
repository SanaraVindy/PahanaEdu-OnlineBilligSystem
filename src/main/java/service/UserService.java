package service;

import java.util.List;
import dao.UserDAO;
import model.User;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public boolean addUser(User user) {
        // Basic validation
        if (user == null || user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty()) {
            return false;
        }
        return userDAO.addUser(user);
    }

    public boolean updateUser(User user) {
        // Basic validation
        if (user == null || user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getEmail().isEmpty()) {
            return false;
        }
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int userID) {
        return userDAO.deleteUser(userID);
    }

    public User getUserByID(int userID) {
        return userDAO.getUserByID(userID);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public List<User> searchUsers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllUsers();
        }
        return userDAO.searchUsers(searchText);
    }
}