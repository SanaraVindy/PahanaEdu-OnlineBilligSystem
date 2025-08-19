package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Order;

// ... (imports)
public class OrderDAO {
    
    // ... (other methods)

    public int createOrder(Order order, Connection conn) throws SQLException {
        int orderID = -1;
        String sql = "INSERT INTO `order` (customerID, totalAmount, orderDate) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getCustomerID());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setTimestamp(3, new java.sql.Timestamp(order.getOrderDate().getTime()));
            
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
}