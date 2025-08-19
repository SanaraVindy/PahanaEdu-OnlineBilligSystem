package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Customer;

public class CustomerDAO {

public List<Customer> getAllCustomers() {
    List<Customer> customers = new ArrayList<>();
    String sql = "SELECT customerID, firstName, lastName, email, contactNo, loyaltyPoints FROM pahanaedu.customer;";
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Customer c = new Customer();
            c.setCustomerID(rs.getInt("customerID"));
            c.setFirstName(rs.getString("firstName"));
            c.setLastName(rs.getString("lastName"));
            c.setEmail(rs.getString("email"));
            c.setContactNo(rs.getString("contactNo"));
            c.setLoyaltyPoints(rs.getInt("loyaltyPoints"));

            customers.add(c);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return customers;
}

    public List<Customer> searchCustomers(String searchText, String mobile) {
        List<Customer> customers = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT customerID, firstName, lastName, email, contactNo, loyaltyPoints FROM pahanaedu.customer WHERE 1=1");

        List<String> params = new ArrayList<>();

        if (searchText != null && !searchText.trim().isEmpty()) {
            sqlBuilder.append(" AND (firstName LIKE ? OR lastName LIKE ? OR email LIKE ?)");
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }

        if (mobile != null && !mobile.trim().isEmpty()) {
            sqlBuilder.append(" AND contactNo LIKE ?");
            params.add("%" + mobile + "%");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerID(rs.getInt("customerID"));
                    c.setFirstName(rs.getString("firstName"));
                    c.setLastName(rs.getString("lastName"));
                    c.setEmail(rs.getString("email"));
                    c.setContactNo(rs.getString("contactNo"));
                    c.setLoyaltyPoints(rs.getInt("loyaltyPoints"));
                    customers.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public boolean insertCustomer(Customer customer) {
        String sql = "INSERT INTO pahanaedu.customer (firstName, lastName, email, contactNo, loyaltyPoints) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getContactNo());
            stmt.setInt(5, customer.getLoyaltyPoints());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(int customerId) {
    String sql = "DELETE FROM pahanaedu.customer WHERE customerID = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, customerId);
        return stmt.executeUpdate() > 0; // returns true if at least one row deleted
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean updateCustomer(Customer customer) {
    String sql = "UPDATE pahanaedu.customer SET firstName = ?, lastName = ?, email = ?, contactNo = ?, loyaltyPoints = ? WHERE customerID = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, customer.getFirstName());
        stmt.setString(2, customer.getLastName());
        stmt.setString(3, customer.getEmail());
        stmt.setString(4, customer.getContactNo());
        stmt.setInt(5, customer.getLoyaltyPoints());
        stmt.setInt(6, customer.getCustomerID());

        return stmt.executeUpdate() > 0; // returns true if updated successfully
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
private static final String UPDATE_LOYALTY_POINTS_SQL = "UPDATE customer SET loyaltyPoints = loyaltyPoints + ? WHERE customerID = ?";

    public boolean updateLoyaltyPoints(int customerID, int pointsToAdd, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_LOYALTY_POINTS_SQL)) {
            ps.setInt(1, pointsToAdd);
            ps.setInt(2, customerID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

}