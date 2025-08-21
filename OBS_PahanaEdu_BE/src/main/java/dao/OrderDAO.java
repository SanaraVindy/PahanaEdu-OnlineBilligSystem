package dao;

import config.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.Order;

/**
 * Data Access Object (DAO) for handling Order-related database operations.
 * It provides methods for creating, retrieving, and managing orders.
 * This class is designed to be used within a transactional context, with the
 * Connection object managed by a higher-level service.
 */
public class OrderDAO {

    /**
     * Creates a new order record in the database.
     * The total amount and discount are now passed as parameters from the
     * BillingService.
     *
     * @param order The Order object containing customer information.
     * @param totalAmount The final calculated total amount for the order.
     * @param discount The total calculated discount for the order.
     * @param conn The database connection to use for the transaction.
     * @return The auto-generated orderID if the creation was successful, or -1 on failure.
     * @throws SQLException if a database access error occurs.
     */
    public int createOrder(Order order, BigDecimal totalAmount, BigDecimal discount, Connection conn) throws SQLException {
        int orderID = -1;
        // Using `totalAmount` to be consistent with the database column name
        String sql = "INSERT INTO `order` (customerID, totalAmount, discount, orderDate) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getCustomerID());
            ps.setBigDecimal(2, totalAmount);
            ps.setBigDecimal(3, discount);
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderID = rs.getInt(1);
                    }
                }
            }
        }
        return orderID;
    }

    /**
     * Retrieves an order from the database by its ID.
     * @param orderID The ID of the order to retrieve.
     * @param conn The database connection to use for the transaction.
     * @return The Order object if found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
    public Order getOrder(int orderID, Connection conn) throws SQLException {
        String sql = "SELECT * FROM `order` WHERE orderID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractOrderFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a list of all orders from the database.
     * @param conn The database connection to use for the transaction.
     * @return A list of Order objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Order> getAllOrders(Connection conn) throws SQLException {
        String sql = "SELECT * FROM `order`";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        }
        return orders;
    }

    /**
     * Updates an existing order record in the database.
     * @param order The Order object with updated data.
     * @param conn The database connection to use for the transaction.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean updateOrder(Order order, Connection conn) throws SQLException {
        String sql = "UPDATE `order` SET customerID = ?, totalAmount = ?, discount = ?, orderDate = ? WHERE orderID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, order.getCustomerID());
            ps.setBigDecimal(2, order.getTotalAmount()); // Using getTotalAmount()
            ps.setBigDecimal(3, order.getDiscount());
            ps.setTimestamp(4, new Timestamp(order.getOrderDate().getTime()));
            ps.setInt(5, order.getOrderID());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Deletes an order record from the database by its ID.
     * @param orderID The ID of the order to delete.
     * @param conn The database connection to use for the transaction.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean deleteOrder(int orderID, Connection conn) throws SQLException {
        String sql = "DELETE FROM `order` WHERE orderID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Helper method to extract order data from a ResultSet.
     * @param rs The ResultSet containing order data.
     * @return A new Order object populated with the data.
     * @throws SQLException if a database access error occurs.
     */
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(rs.getInt("orderID"));
        order.setCustomerID(rs.getInt("customerID"));
        order.setOrderDate(new Date(rs.getTimestamp("orderDate").getTime()));
        order.setTotalAmount(rs.getBigDecimal("totalAmount")); // Using setTotalAmount()
        order.setDiscount(rs.getBigDecimal("discount"));
        return order;
    }
}