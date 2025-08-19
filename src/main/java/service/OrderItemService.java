// service/OrderItemService.java
package service;

import dao.OrderItemDAO;
import model.OrderItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderItemService {
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    public boolean addOrderItems(List<OrderItem> orderItems, Connection conn) throws SQLException {
        return orderItemDAO.addOrderItems(orderItems, conn);
    }
}