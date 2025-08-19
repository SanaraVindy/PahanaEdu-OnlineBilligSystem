package controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Report;
import service.ReportService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet acting as the API endpoint for fetching reports. It processes GET
 * requests and returns a JSON response.
 */
@WebServlet("/api/top-customers")
public class ReportController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Now the controller depends on the service layer, not the DAO.
    private ReportService reportService = new ReportService();

    // Create a Gson instance for converting Java objects to JSON.
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set the response content type to application/json.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Get query parameters from the request.
            String fromDate = request.getParameter("startDate");
            String toDate = request.getParameter("endDate");
            System.out.println(fromDate);
            System.out.println(toDate);
            // Handle the case where the limit parameter is null or invalid.
            int customerLimit;
            String limitParam = request.getParameter("limit");
            if (limitParam == null || limitParam.trim().isEmpty()) {
                customerLimit = 10; // Default limit if not provided
            } else {
                customerLimit = Integer.parseInt(limitParam);
            }
            System.out.println(limitParam);
            // Call the service layer to get the data.
            List<Report> reports = reportService.getTopCustomersReport(fromDate, toDate, customerLimit);

            // Convert the list of Report objects to a JSON string.
            String jsonResponse = gson.toJson(reports);

            // Write the JSON response to the output stream.
            response.getWriter().write(jsonResponse);

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 status code
            response.getWriter().write("{\"error\": \"Invalid number format for customer limit.\"}");
        } catch (SQLException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, "Database error while fetching report.", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 status code
            response.getWriter().write("{\"error\": \"A database error occurred.\"}");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, "An unexpected error occurred.", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 status code
            response.getWriter().write("{\"error\": \"An unexpected error occurred.\"}");
        }
    }
}
