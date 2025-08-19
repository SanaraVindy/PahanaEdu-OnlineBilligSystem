package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Invoice;
import java.math.BigDecimal;
import java.util.Date;

public class InvoiceDAO {

    /**
     * Creates a new invoice record in the database.
     * The SQL statement has been updated to use the 'paymentType' column.
     * @param invoice The Invoice object containing the data to be inserted.
     * @return true if the invoice was created successfully, false otherwise.
     */
   public boolean createInvoice(Connection conn, Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoice (orderID, invoiceDate, totalAmount, discount, paymentType) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoice.getOrderID());
            ps.setTimestamp(2, new Timestamp(invoice.getInvoiceDate().getTime()));
            ps.setBigDecimal(3, invoice.getTotalAmount());
            ps.setBigDecimal(4, invoice.getDiscount());
            ps.setString(5, invoice.getPaymentType());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves an invoice from the database by its ID.
     * @param invoiceID The ID of the invoice to retrieve.
     * @return The Invoice object if found, otherwise null.
     */
    public Invoice getInvoiceById(int invoiceID) {
        String sql = "SELECT * FROM invoice WHERE invoiceID = ?";
        Invoice invoice = null;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, invoiceID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    invoice = new Invoice();
                    invoice.setInvoiceID(rs.getInt("invoiceID"));
                    invoice.setOrderID(rs.getInt("orderID"));
                    invoice.setInvoiceDate(new Date(rs.getTimestamp("invoiceDate").getTime()));
                    invoice.setTotalAmount(rs.getBigDecimal("totalAmount"));
                    invoice.setDiscount(rs.getBigDecimal("discount"));
                    invoice.setPaymentType(rs.getString("paymentType"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoice;
    }

    /**
     * Retrieves a list of all invoices from the database.
     * @return A list of Invoice objects.
     */
    public List<Invoice> getAllInvoices() {
        String sql = "SELECT * FROM invoice";
        List<Invoice> invoices = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceID(rs.getInt("invoiceID"));
                invoice.setOrderID(rs.getInt("orderID"));
                invoice.setInvoiceDate(new Date(rs.getTimestamp("invoiceDate").getTime()));
                invoice.setTotalAmount(rs.getBigDecimal("totalAmount"));
                invoice.setDiscount(rs.getBigDecimal("discount"));
                invoice.setPaymentType(rs.getString("paymentType"));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    /**
     * Updates an existing invoice record in the database.
     * @param invoice The Invoice object with updated data.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE invoice SET orderID = ?, invoiceDate = ?, totalAmount = ?, discount = ?, paymentType = ? WHERE invoiceID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, invoice.getOrderID());
            ps.setTimestamp(2, new Timestamp(invoice.getInvoiceDate().getTime()));
            ps.setBigDecimal(3, invoice.getTotalAmount());
            ps.setBigDecimal(4, invoice.getDiscount());
            ps.setString(5, invoice.getPaymentType());
            ps.setInt(6, invoice.getInvoiceID());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an invoice record from the database by its ID.
     * @param invoiceID The ID of the invoice to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteInvoice(int invoiceID) {
        String sql = "DELETE FROM invoice WHERE invoiceID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, invoiceID);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
