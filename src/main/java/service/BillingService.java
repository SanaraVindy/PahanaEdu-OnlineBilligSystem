package service;

import config.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import model.Order;
import model.OrderItem;
import model.Invoice; // <-- Add this import
import java.util.Date; // <-- Add this import
import java.util.List;

public class BillingService {

    private final OrderService orderService = new OrderService();
    private final OrderItemService orderItemService = new OrderItemService();
    private final CustomerService customerService = new CustomerService();
    private final ItemService itemService = new ItemService();
    private final InvoiceService invoiceService = new InvoiceService();

    /**
     * Processes a new transaction by creating an order, adding its items,
     * updating item quantities, updating customer loyalty points, and creating
     * an invoice within a single database transaction.
     * @param order The Order object to be created.
     * @param orderItems A list of OrderItem objects for the order.
     * @return true if the transaction is successful, false otherwise.
     */
    public boolean processTransaction(Order order, List<OrderItem> orderItems, BigDecimal discount, String paymentMethod) {
        Connection conn = null;
        boolean success = false;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Create the order and get the generated ID
            int orderID = orderService.createOrder(order, conn);

            if (orderID != -1) {
                // 2. Set the generated orderID to all order items and add them
                for (OrderItem item : orderItems) {
                    item.setOrderID(orderID);
                }

                boolean itemsAdded = orderItemService.addOrderItems(orderItems, conn);

                if (itemsAdded) {
                    // 3. Update the quantities of the purchased items
                    boolean itemsUpdated = itemService.updateItemQuantities(conn, orderItems);

                    if (itemsUpdated) {
                        // 4. Calculate and update loyalty points
                        int pointsToAdd = order.getTotalAmount().intValue() / 10;
                        boolean pointsUpdated = customerService.updateLoyaltyPoints(order.getCustomerID(), pointsToAdd, conn);

                        if (pointsUpdated) {
                            // 5. Create the invoice
                            Invoice invoice = new Invoice();
                            invoice.setOrderID(orderID);
                            invoice.setInvoiceDate(new Date());
                            invoice.setTotalAmount(order.getTotalAmount());
                            invoice.setDiscount(discount);
                            invoice.setPaymentType(paymentMethod);
                            
                            System.out.println(orderID);
                            System.out.println(paymentMethod);
                            //System.out.println(order.TotalAmount);
                            System.out.println(discount);
                            
                            boolean invoiceCreated = invoiceService.createInvoice(conn, invoice);

                            if (invoiceCreated) {
                                conn.commit(); // Commit transaction
                                success = true;
                            } else {
                                conn.rollback(); // Rollback if invoice creation fails
                            }
                        } else {
                            conn.rollback(); // Rollback if loyalty points fail
                        }
                    } else {
                        conn.rollback(); // Rollback if updating item quantities fails
                    }
                } else {
                    conn.rollback(); // Rollback if adding items fails
                }
            } else {
                conn.rollback(); // Rollback if creating order fails
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on any SQL exception
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }
}