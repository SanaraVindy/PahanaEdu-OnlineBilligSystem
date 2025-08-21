package service;

import config.DBConnection;
import dao.InvoiceDAO;
import dao.ItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import model.Invoice;
import model.Item;
import model.Order;
import model.OrderItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BillingService {

    private final OrderService orderService;
    private final OrderItemDAO orderItemDAO;
    private final InvoiceDAO invoiceDAO;
    private final ItemDAO itemDAO;

    // Constructor with dependency injection.
    public BillingService(OrderService orderService, OrderItemDAO orderItemDAO, InvoiceDAO invoiceDAO, ItemDAO itemDAO) {
        this.orderService = orderService;
        this.orderItemDAO = orderItemDAO;
        this.invoiceDAO = invoiceDAO;
        this.itemDAO = itemDAO;
    }

    public String processTransaction(Order order, List<OrderItem> orderItems, String paymentType) {
        if (orderItems == null || orderItems.isEmpty()) {
            return "Error: Order items list is null or empty.";
        }
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Step 1: Calculate total amount and discount
            BigDecimal subtotal = calculateSubtotal(orderItems, conn);
            BigDecimal discount = calculateDiscount(subtotal, orderItems, conn); 
            BigDecimal grandTotal = subtotal.subtract(discount);

            // FIX: Set the calculated totalAmount and discount on the Order object
            order.setTotalAmount(grandTotal);
            order.setDiscount(discount);

            // Step 2: Create the Order and get the generated order ID
            int orderId = orderService.createOrder(order, grandTotal, discount, conn);
            if (orderId == -1) {
                conn.rollback();
                return "Error: Failed to add order.";
            }

            // Step 3: Add order items for the new order
            orderItems.forEach(item -> item.setOrderID(orderId));
            boolean itemsAdded = orderItemDAO.addOrderItems(orderItems, conn);
            if (!itemsAdded) {
                conn.rollback();
                return "Error: Failed to add order items.";
            }

            // Step 4: Create the invoice
            Invoice invoice = new Invoice();
            invoice.setOrderID(orderId);
            invoice.setTotalAmount(grandTotal);
            invoice.setDiscount(discount);
            invoice.setPaymentType(paymentType);
            int invoiceId = invoiceDAO.createInvoice(conn, invoice);
            if (invoiceId == -1) {
                conn.rollback();
                return "Error: Failed to create invoice.";
            }

            // Step 5: Update customer loyalty points (if applicable)
            // This is a placeholder for future implementation.

            conn.commit(); // Commit the transaction
            return "Transaction successful";

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException ex) {
                System.err.println("Rollback failed.");
            }
            return "Error: Transaction failed due to a database error. " + e.getMessage();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection.");
            }
        }
    }

    private BigDecimal calculateSubtotal(List<OrderItem> orderItems, Connection conn) throws SQLException {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            Item item = itemDAO.getItem(orderItem.getItemID(), conn);
            if (item != null) {
                subtotal = subtotal.add(item.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            }
        }
        return subtotal;
    }

    // Corrected to handle SQLException without a RuntimeException wrapper
    private BigDecimal calculateDiscount(BigDecimal subtotal, List<OrderItem> orderItems, Connection conn) throws SQLException {
        BigDecimal totalDiscountPercentage = BigDecimal.ZERO;
        
        if (subtotal.compareTo(new BigDecimal("7000")) >= 0) {
            totalDiscountPercentage = totalDiscountPercentage.add(new BigDecimal("10"));
        }

        boolean hasChildrensItem = false;
        for (OrderItem orderItem : orderItems) {
            Item item = itemDAO.getItem(orderItem.getItemID(), conn);
            if (item != null && item.getCategoryID() == 7) {
                hasChildrensItem = true;
                break;
            }
        }

        if (hasChildrensItem) {
            totalDiscountPercentage = totalDiscountPercentage.add(new BigDecimal("5"));
        }
        
        // This method does not implement the loyalty discount rule
        
        return subtotal.multiply(totalDiscountPercentage.divide(new BigDecimal("100")));
    }
}