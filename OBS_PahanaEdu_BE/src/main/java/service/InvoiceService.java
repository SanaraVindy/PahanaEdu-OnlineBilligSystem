package service;

import dao.InvoiceDAO;
import java.sql.Connection;
import java.sql.SQLException;
import model.Invoice;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO;

    // Use a constructor for dependency injection
    public InvoiceService(InvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }

    /**
     * Creates a new invoice and returns the generated invoice ID.
     *
     * @param conn The database connection.
     * @param invoice The Invoice object to be created.
     * @return The auto-generated invoiceID if successful, otherwise -1.
     * @throws SQLException if a database access error occurs.
     */
    public int createInvoice(Connection conn, Invoice invoice) throws SQLException {
        // The DAO now returns the generated invoice ID, so the service method should too.
        return invoiceDAO.createInvoice(conn, invoice);
    }
}