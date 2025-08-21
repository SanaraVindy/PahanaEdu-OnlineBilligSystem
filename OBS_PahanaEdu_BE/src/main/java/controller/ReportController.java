package controller;

import com.google.gson.Gson;
import dao.ReportDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST API for generating various reports.
 * This class provides endpoints for top customers and monthly sales summaries.
 */
@Path("/api/reports")
public class ReportController {

    private static final Logger LOGGER = Logger.getLogger(ReportController.class.getName());
    private ReportDAO reportDAO = new ReportDAO();
    private final Gson gson = new Gson();

    /**
     * Setter for the ReportDAO. This is a good pattern for dependency injection,
     * which makes unit testing much easier.
     * @param reportDAO The ReportDAO instance to set.
     */
    public void setReportDAO(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    /**
     * Endpoint to get a report of top customers.
     * Accessible via GET request to /reports/top-customers.
     *
     * @param fromDate The start date for the report.
     * @param toDate The end date for the report.
     * @param customerLimit The number of top customers to return.
     * @return A JSON response containing the list of top customers or an error message.
     */
    @GET
    @Path("/top-customers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopCustomersReport(@QueryParam("fromDate") String fromDate,
                                          @QueryParam("toDate") String toDate,
                                          @QueryParam("limit") int customerLimit) {
        // Good use of logging to trace incoming requests.
        LOGGER.log(Level.INFO, "Request received for getTopCustomersReport with fromDate: {0}, toDate: {1}, limit: {2}", new Object[]{fromDate, toDate, customerLimit});
        try {
            // Excellent input validation at the API entry point.
            if (fromDate == null || toDate == null || fromDate.isEmpty() || toDate.isEmpty()) {
                LOGGER.log(Level.WARNING, "Invalid input: fromDate or toDate is missing.");
                return Response.status(Response.Status.BAD_REQUEST).entity("From date and to date are required.").build();
            }

            // Retrieve the report data from the DAO.
            // NOTE: The code in the `ReportService` already performs validation.
            // You could call the service method here instead of the DAO directly
            // to maintain a consistent API flow.
            // Example: var reports = new ReportService().getTopCustomersReport(fromDate, toDate, customerLimit);
            var reports = reportDAO.getTopCustomersReport(fromDate, toDate, customerLimit);
            
            // Return a success response with the report data in JSON format.
            return Response.ok(gson.toJson(reports)).build();
        } catch (SQLException e) {
            // Proper handling for database-specific exceptions.
            LOGGER.log(Level.SEVERE, "Database error occurred while fetching top customers report.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + e.getMessage()).build();
        } catch (Exception e) {
            // A catch-all for other unexpected exceptions.
            LOGGER.log(Level.SEVERE, "An unexpected error occurred while fetching top customers report.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred.").build();
        }
    }

    /**
     * Endpoint to get a monthly sales summary report.
     * Accessible via GET request to /reports/monthly-sales-summary.
     *
     * @param year The year for which to generate the report.
     * @return A JSON response containing the monthly sales summary data or an error message.
     */
    @GET
    @Path("/monthly-sales-summary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlySalesSummaryReport(@QueryParam("year") String year) {
        LOGGER.log(Level.INFO, "Request received for getMonthlySalesSummaryReport for year: {0}", year);
        try {
            // Good validation for the year parameter.
            if (year == null || year.isEmpty()) {
                LOGGER.log(Level.WARNING, "Invalid input: year is missing.");
                return Response.status(Response.Status.BAD_REQUEST).entity("Year parameter is required.").build();
            }

            // You could call the service method here instead of the DAO directly.
            // Example: var summary = new ReportService().getMonthlySalesSummary(year);
            var summary = reportDAO.getMonthlySalesSummary(year);

            // Return a success response with the summary data in JSON format.
            return Response.ok(gson.toJson(summary)).build();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error occurred while fetching monthly sales summary.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + e.getMessage()).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred while fetching monthly sales summary.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred.").build();
        }
    }
}
