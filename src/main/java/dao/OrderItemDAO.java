package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import model.OrderItem;

public class OrderItemDAO {

    // The SQL query is updated to match the new database schema without 'priceAtPurchase'.
    private static final String INSERT_ORDER_ITEM_SQL = 
            "INSERT INTO order_item (orderID, itemID, quantity) VALUES (?, ?, ?)";

    /**
     * Adds a list of OrderItem objects to the database using a batch operation.
     * @param orderItems A list of OrderItem objects to be added.
     * @param conn The database connection.
     * @return true if all items were added successfully, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean addOrderItems(List<OrderItem> orderItems, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_ORDER_ITEM_SQL)) {
            for (OrderItem item : orderItems) {
                // Set the parameters for the prepared statement.
                // Parameter 1: orderID
                ps.setInt(1, item.getOrderID());
                // Parameter 2: itemID
                ps.setInt(2, item.getItemID());
                // Parameter 3: quantity
                ps.setInt(3, item.getQuantity());

                // Add the current set of parameters to the batch.
                ps.addBatch();
            }
            int[] rowsAffected = ps.executeBatch();
            for (int count : rowsAffected) {
                if (count <= 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
