package dao;

import config.DBConnection;
import model.Report;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling report-related database operations.
 */
public class ReportDAO {
    
    /**
     * Executes the get_top_customers_report stored procedure and returns the results.
     * @param fromDate The start date for the report.
     * @param toDate The end date for the report.
     * @param customerLimit The number of top customers to return.
     * @return A list of Report objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Report> getTopCustomersReport(String fromDate, String toDate, int customerLimit) throws SQLException {
        List<Report> reports = new ArrayList<>();
        // Use a try-with-resources statement to ensure the connection and statement are closed.
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL get_top_customers_report(?, ?, ?)}")) {

            // Set the input parameters for the stored procedure.
            cs.setString(1, fromDate);
            cs.setString(2, toDate);
            cs.setInt(3, customerLimit);

            // Execute the query and get the result set.
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Report report = new Report();
                    report.setRank(rs.getInt("rank_number"));
                    report.setFirstName(rs.getString("firstName"));
                    report.setLastName(rs.getString("lastName"));
                    report.setEmail(rs.getString("email"));
                    report.setTotalOrders(rs.getInt("totalOrders"));
                    report.setTotalSpent(rs.getDouble("totalSpent"));
                    report.setLoyaltyPoints(rs.getInt("loyaltyPoints"));
                    report.setTopCategory(rs.getString("topCategory"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }
}
