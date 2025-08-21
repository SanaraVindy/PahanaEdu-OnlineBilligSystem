package service;

import dao.OrderDAO;
import model.Order;
import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;

public class OrderService {

    private final OrderDAO orderDAO;

    // The BillingService will instantiate this with a mock or a real DAO
    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    /**
     * Creates a new order by delegating the call to the OrderDAO.
     * This method is part of a larger transaction managed by BillingService,
     * so it requires an existing Connection object.
     *
     * @param order      The Order object to be created.
     * @param grandTotal The total amount of the order after discounts.
     * @param discount   The total discount applied to the order.
     * @param conn       The database connection for the transaction.
     * @return The auto-generated order ID, or -1 if the creation failed.
     * @throws SQLException if a database access error occurs.
     */
    public int createOrder(Order order, BigDecimal grandTotal, BigDecimal discount, Connection conn) throws SQLException {
        // Correctly calls the DAO method with the required four arguments
        return orderDAO.createOrder(order, grandTotal, discount, conn);
    }
}