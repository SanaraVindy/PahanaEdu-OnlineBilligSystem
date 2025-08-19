package service;

import dao.ReportDAO;
import model.Report;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling all business logic related to reports.
 * It acts as an intermediary between the controller and the DAO layer.
 */
public class ReportService {
    
    // The service layer holds an instance of the DAO.
    private ReportDAO reportDAO;

    // Constructor to initialize the DAO.
    public ReportService() {
        this.reportDAO = new ReportDAO();
    }
    
    /**
     * Retrieves the top customers report from the data access layer.
     * This method can include additional business logic if needed in the future,
     * such as caching or data manipulation before returning to the controller.
     * @param fromDate The start date for the report.
     * @param toDate The end date for the report.
     * @param customerLimit The number of top customers to return.
     * @return A list of Report objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Report> getTopCustomersReport(String fromDate, String toDate, int customerLimit) throws SQLException {
        // You could add validation or other business logic here before calling the DAO.
        if (fromDate == null || fromDate.trim().isEmpty() || toDate == null || toDate.trim().isEmpty() || customerLimit < 1) {
             throw new IllegalArgumentException("Invalid input parameters for report generation.");
        }
        
        // Simply delegate the call to the DAO to fetch the data.
        return reportDAO.getTopCustomersReport(fromDate, toDate, customerLimit);
    }
}
