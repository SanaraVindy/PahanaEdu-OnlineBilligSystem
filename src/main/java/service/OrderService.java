package service;

import config.DBConnection;
import dao.OrderDAO;
import model.Order;
import java.sql.Connection;
import java.sql.SQLException;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();

    // This is the only createOrder method you need for this transaction
    public int createOrder(Order order, Connection conn) throws SQLException {
        // Correctly calls the DAO method to create the order
        return orderDAO.createOrder(order, conn);
    }
}