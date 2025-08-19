package service;

import dao.InvoiceDAO;
import java.sql.Connection;
import java.sql.SQLException;
import model.Invoice;


public class InvoiceService {
    
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    public boolean createInvoice(Connection conn, Invoice invoice) throws SQLException {
        return invoiceDAO.createInvoice(conn, invoice);
    }
}