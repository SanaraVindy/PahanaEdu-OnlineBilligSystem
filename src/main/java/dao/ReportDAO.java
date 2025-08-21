package dao;

import config.DBConnection;
import model.MonthlySalesSummary;
import model.Report;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling various report-related database operations.
 */
public class ReportDAO {

    /**
     * Executes the GetTopCustomersAndItemSummary stored procedure and returns the results.
     * @param fromDate The start date for the report.
     * @param toDate The end date for the report.
     * @param customerLimit The number of top customers to return.
     * @return A list of Report objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Report> getTopCustomersReport(String fromDate, String toDate, int customerLimit) throws SQLException {
        List<Report> reports = new ArrayList<>();
        // Use a try-with-resources statement to ensure the connection and statement are closed.
        // NOTE: The stored procedure name has been corrected to match your SQL.
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL GetTopCustomersAndItemSummary(?, ?, ?)}")) {

            // Set the input parameters for the stored procedure.
            cs.setString(1, fromDate);
            cs.setString(2, toDate);
            cs.setInt(3, customerLimit);

            // Execute the query and get the result set.
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Report report = new Report();
                    // NOTE: The column name for rank has been corrected to "Rank".
                    report.setRank(rs.getInt("Rank")); 
                    report.setFirstName(rs.getString("firstName"));
                    report.setLastName(rs.getString("lastName"));
                    report.setEmail(rs.getString("email"));
                    report.setTotalOrders(rs.getInt("TotalOrders"));
                    report.setTotalSpent(rs.getDouble("TotalSpent"));
                    report.setLoyaltyPoints(rs.getInt("loyaltyPoints"));
                    report.setTopCategory(rs.getString("TopCategory"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    /**
     * Retrieves a monthly sales summary from the database by calling a stored procedure.
     * @param year The year for the report.
     * @return A list of MonthlySalesSummary objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<MonthlySalesSummary> getMonthlySalesSummary(String year) throws SQLException {
        List<MonthlySalesSummary> summary = new ArrayList<>();
        // Use a try-with-resources statement to ensure the connection and statement are closed.
        // This replaces the placeholder data with a real database call.
        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL GetMonthlySalesSummary(?)}")) {
            
            // Set the input parameter for the stored procedure.
            cs.setString(1, year);

            // Execute the query and get the result set.
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    // Populate the MonthlySalesSummary object with data from the result set.
                    MonthlySalesSummary monthlySummary = new MonthlySalesSummary();
                    monthlySummary.setMonth(rs.getString("Month"));
                    monthlySummary.setTotalRevenue(rs.getDouble("TotalRevenue"));
                    monthlySummary.setTransactionCount(rs.getInt("TransactionCount"));
                    summary.add(monthlySummary);
                }
            }
        }
        return summary;
    }
}
