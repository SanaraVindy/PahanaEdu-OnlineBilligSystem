package service;

import dao.ReportDAO;
import model.MonthlySalesSummary;
import model.Report;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReportService {

    // The service layer holds an instance of the DAO.
    private ReportDAO reportDAO;

    // Constructor to initialize the DAO.
    public ReportService() {
        this.reportDAO = new ReportDAO();
    }

    
    public List<Report> getTopCustomersReport(String fromDate, String toDate, int customerLimit) throws SQLException {
        if (fromDate == null || fromDate.trim().isEmpty() || toDate == null || toDate.trim().isEmpty() || customerLimit < 1) {
            throw new IllegalArgumentException("Invalid input parameters for top customers report.");
        }
        return reportDAO.getTopCustomersReport(fromDate, toDate, customerLimit);
    }

    
    public List<MonthlySalesSummary> getMonthlySalesSummary(String year) throws SQLException {
        if (year == null || year.trim().isEmpty() || !year.matches("\\d{4}")) {
            throw new IllegalArgumentException("Invalid year provided. Year must be a 4-digit number.");
        }
        return reportDAO.getMonthlySalesSummary(year);
    }
  

    public void getMonthlySalesSummaryReport(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
