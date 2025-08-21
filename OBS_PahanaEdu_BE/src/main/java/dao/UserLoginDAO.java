package dao;


import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLoginDAO {

    /**
     * Retrieves the hashed password, user ID, and password change status for a given username.
     * @param username The username to look up.
     * @return An array containing the hashed password, user ID, and passwordChanged status, or null if not found.
     */
    public String[] getPasswordDetailsByUsername(String username) {
        String query = "SELECT password, userID, passwordChanged FROM userlogin WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{
                        rs.getString("password"),
                        String.valueOf(rs.getInt("userID")),
                        String.valueOf(rs.getBoolean("passwordChanged"))
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the password and sets the passwordChanged flag to true.
     * @param userID The user's ID.
     * @param newHashedPassword The new SHA-256 hashed password.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updatePassword(int userID, String newHashedPassword) {
        String query = "UPDATE userlogin SET password = ?, passwordChanged = 1 WHERE userID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, newHashedPassword);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}