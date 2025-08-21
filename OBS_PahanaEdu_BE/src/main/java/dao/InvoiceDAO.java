package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.math.BigDecimal;

import model.Invoice;

/**
 * Data Access Object (DAO) for handling Invoice-related database operations.
 * This class is designed to be used within a transactional context, with the
 * Connection object managed by a higher-level service (e.g., BillingService).
 */
public class InvoiceDAO {

    /**
     * Creates a new invoice record in the database.
     * * @param conn The database connection to use for the transaction.
     * @param invoice The Invoice object containing the data to be inserted.
     * @return The auto-generated invoiceID if the creation was successful, otherwise -1.
     * @throws SQLException if a database access error occurs.
     */
    public int createInvoice(Connection conn, Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoice (orderID, invoiceDate, totalAmount, discount, paymentType) VALUES (?, ?, ?, ?, ?)";
        int generatedId = -1;

        // The try-with-resources statement ensures the PreparedStatement is closed automatically.
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, invoice.getOrderID());
            
            // Fix: Use the current timestamp if the invoice date is not set.
            Date invoiceDate = (invoice.getInvoiceDate() != null) ? invoice.getInvoiceDate() : new Date();
            ps.setTimestamp(2, new Timestamp(invoiceDate.getTime()));
            
            ps.setBigDecimal(3, invoice.getTotalAmount());
            ps.setBigDecimal(4, invoice.getDiscount());
            ps.setString(5, invoice.getPaymentType());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1); // Retrieve the auto-generated ID
                    }
                }
            }
        }
        return generatedId;
    }

    /**
     * Retrieves an invoice from the database by its ID.
     * This method is part of a larger transaction managed by the service.
     * * @param conn The database connection to use.
     * @param invoiceID The ID of the invoice to retrieve.
     * @return The Invoice object if found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
    public Invoice getInvoiceById(Connection conn, int invoiceID) throws SQLException {
        String sql = "SELECT * FROM invoice WHERE invoiceID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractInvoiceFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a list of all invoices from the database.
     * This method is part of a larger transaction managed by the service.
     * * @param conn The database connection to use.
     * @return A list of Invoice objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Invoice> getAllInvoices(Connection conn) throws SQLException {
        String sql = "SELECT * FROM invoice";
        List<Invoice> invoices = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                invoices.add(extractInvoiceFromResultSet(rs));
            }
        }
        return invoices;
    }

    /**
     * Updates an existing invoice record in the database.
     * This method is part of a larger transaction managed by the service.
     * * @param conn The database connection to use.
     * @param invoice The Invoice object with updated data.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean updateInvoice(Connection conn, Invoice invoice) throws SQLException {
        String sql = "UPDATE invoice SET orderID = ?, invoiceDate = ?, totalAmount = ?, discount = ?, paymentType = ? WHERE invoiceID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoice.getOrderID());
            
            Date invoiceDate = (invoice.getInvoiceDate() != null) ? invoice.getInvoiceDate() : new Date();
            ps.setTimestamp(2, new Timestamp(invoiceDate.getTime()));
            
            ps.setBigDecimal(3, invoice.getTotalAmount());
            ps.setBigDecimal(4, invoice.getDiscount());
            ps.setString(5, invoice.getPaymentType());
            ps.setInt(6, invoice.getInvoiceID());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Deletes an invoice record from the database by its ID.
     * This method is part of a larger transaction managed by the service.
     * * @param conn The database connection to use.
     * @param invoiceID The ID of the invoice to delete.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean deleteInvoice(Connection conn, int invoiceID) throws SQLException {
        String sql = "DELETE FROM invoice WHERE invoiceID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceID);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Helper method to extract invoice data from a ResultSet.
     * @param rs The ResultSet containing invoice data.
     * @return A new Invoice object populated with the data.
     * @throws SQLException if a database access error occurs.
     */
    private Invoice extractInvoiceFromResultSet(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceID(rs.getInt("invoiceID"));
        invoice.setOrderID(rs.getInt("orderID"));
        invoice.setInvoiceDate(new Date(rs.getTimestamp("invoiceDate").getTime()));
        invoice.setTotalAmount(rs.getBigDecimal("totalAmount"));
        invoice.setDiscount(rs.getBigDecimal("discount"));
        invoice.setPaymentType(rs.getString("paymentType"));
        return invoice;
    }
}