package service;

import dao.InvoiceDAO;
import dao.ItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import model.Invoice;
import model.Item;
import model.Order;
import model.OrderItem;
import config.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for handling billing and transaction processes.
 * It coordinates with various DAOs to ensure the atomicity of billing operations.
 */
public class BillingService {
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final InvoiceDAO invoiceDAO;
    private final ItemDAO itemDAO;

    public BillingService(OrderDAO orderDAO, OrderItemDAO orderItemDAO, InvoiceDAO invoiceDAO, ItemDAO itemDAO) {
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.invoiceDAO = invoiceDAO;
        this.itemDAO = itemDAO;
    }

    /**
     * Processes a complete billing transaction, including creating an order, adding order items,
     * applying discounts, and creating an invoice. The entire operation is wrapped in a single database transaction.
     *
     * @param order The Order object containing customer and total details.
     * @param orderItems A list of OrderItem objects representing the items in the order.
     * @param grandTotal The total amount of the order before any discounts.
     * @param paymentType The method of payment.
     * @return A status message indicating the result of the transaction (e.g., "Transaction successful" or an error message).
     */
    public String processTransaction(Order order, List<OrderItem> orderItems, BigDecimal grandTotal, String paymentType) {
        if (orderItems == null || orderItems.isEmpty()) {
            return "Error: Order items list is null or empty.";
        }
        
        // This is a crucial change: we get the connection here and pass it to the DAOs.
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false); // Begin transaction

            try {
                // 1. Apply discounts
                BigDecimal totalDiscount = BigDecimal.ZERO;

                // Rule 1: 10% discount if grand total is over 5000
                if (grandTotal.compareTo(new BigDecimal("5000.00")) > 0) {
                    totalDiscount = totalDiscount.add(grandTotal.multiply(new BigDecimal("0.10")));
                }

                // Rule 2: 5% discount if any item is from category ID 7 (Children's wear)
                boolean hasChildrensWear = false;
                for (OrderItem item : orderItems) {
                    Item fetchedItem = itemDAO.getItem(item.getItemID(), con);
                    if (fetchedItem != null && fetchedItem.getCategoryID() == 7) {
                        hasChildrensWear = true;
                        break;
                    }
                }
                if (hasChildrensWear) {
                    totalDiscount = totalDiscount.add(grandTotal.multiply(new BigDecimal("0.05")));
                }

                BigDecimal finalTotal = grandTotal.subtract(totalDiscount);
                
                // 2. Create the order
                int newOrderID = orderDAO.createOrder(order, con);
                if (newOrderID == -1) {
                    con.rollback();
                    return "Error: Failed to add order.";
                }

                // 3. Add order items
                for (OrderItem item : orderItems) {
                    item.setOrderID(newOrderID);
                }
                boolean itemsAdded = orderItemDAO.addOrderItems(orderItems, con);
                if (!itemsAdded) {
                    con.rollback();
                    return "Error: Failed to add order items.";
                }

                // 4. Create the invoice
                Invoice invoice = new Invoice();
                invoice.setOrderID(newOrderID);
                invoice.setTotalAmount(finalTotal);
                invoice.setDiscount(totalDiscount);
                invoice.setPaymentType(paymentType);

                boolean invoiceCreated = invoiceDAO.createInvoice(con, invoice);
                if (!invoiceCreated) {
                    con.rollback();
                    return "Error: Failed to add invoice.";
                }

                con.commit(); // Commit transaction
                return "Transaction successful";
            } catch (SQLException e) {
                con.rollback();
                return "Error: Transaction failed due to a database error. " + e.getMessage();
            }
        } catch (SQLException e) {
            return "Error: Failed to establish a database connection. " + e.getMessage();
        }
    }
}
