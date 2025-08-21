package Test;

import controller.ReportController;
import dao.ReportDAO;
import jakarta.ws.rs.core.Response;
import model.MonthlySalesSummary;
import model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit test class for the JAX-RS ReportController.
 * This test uses Mockito to isolate the controller logic from its dependencies,
 * specifically the ReportDAO. It verifies that the controller methods return
 * the correct HTTP status codes and JSON data.
 */
@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    // The class we want to test.
    @InjectMocks
    private ReportController reportController;

    // We mock the DAO dependency so we can control its behavior during the test.
    @Mock
    private ReportDAO reportDAO;

    // Use Gson to verify the JSON content in the test responses.
    private Gson gson;

    @BeforeEach
    void setUp() {

    @Test
    void testGetTopCustomersReport_Success() throws SQLException {
        // Prepare mock data that the DAO will return.
        var mockReports = Collections.singletonList(
            new Report(1, "John", "Doe", "john.doe@example.com", 5, 1500.0, 50, "Electronics")
        );
        // Configure the mock DAO to return the mock data when its method is called.
        when(reportDAO.getTopCustomersReport("2024-01-01", "2024-12-31", 10)).thenReturn(mockReports);

        // Call the controller method directly, as it's a JAX-RS endpoint.
        Response response = reportController.getTopCustomersReport("2024-01-01", "2024-12-31", 10);

        // Verify that the DAO method was called exactly once with the correct parameters.
        verify(reportDAO, times(1)).getTopCustomersReport("2024-01-01", "2024-12-31", 10);

        // Assert that the HTTP response status is OK (200).
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Get the response body as a JSON string and parse it back into a list.
        String jsonResponse = (String) response.getEntity();
        Type listType = new TypeToken<List<Report>>() {}.getType();
        List<Report> resultReports = gson.fromJson(jsonResponse, listType);

        // Assert the content of the response.
        assertEquals(1, resultReports.size());
        Report resultReport = resultReports.get(0);
        assertEquals("John", resultReport.getFirstName());
        assertEquals(1500.0, resultReport.getTotalSpent());
    }

    /**
     * Test case for the 'Monthly Sales Summary' report generation.
     * This test ensures the controller returns the correct monthly sales data.
     */
    @Test
    void testGetMonthlySalesSummaryReport_Success() throws SQLException {
        // Prepare mock data for the monthly sales summary.
        var mockSummary = Collections.singletonList(
            new MonthlySalesSummary("January", 50000.0, 150)
        );
        // Configure the mock DAO.
        when(reportDAO.getMonthlySalesSummary("2024")).thenReturn(mockSummary);

        // Call the controller method directly.
        Response response = reportController.getMonthlySalesSummaryReport("2024");

        // Verify the DAO method was called with the correct year.
        verify(reportDAO, times(1)).getMonthlySalesSummary("2024");

        // Assert the response status and content.
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String jsonResponse = (String) response.getEntity();
        Type listType = new TypeToken<List<MonthlySalesSummary>>() {}.getType();
        List<MonthlySalesSummary> resultSummaries = gson.fromJson(jsonResponse, listType);

        // Assert the content of the deserialized list.
        assertEquals(1, resultSummaries.size());
        MonthlySalesSummary resultSummary = resultSummaries.get(0);
        assertEquals("January", resultSummary.getMonth());
        assertEquals(50000.0, resultSummary.getTotalRevenue());
    }

    /**
     * Test case for when required parameters are missing.
     * The controller should return a 400 Bad Request status.
     */
    @Test
    void testGetReport_MissingParameters() throws SQLException {
        // Call the method with a null parameter to simulate a missing request parameter.
        Response response = reportController.getTopCustomersReport(null, "2024-12-31", 10);

        // Verify that the status is 400 Bad Request.
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        // Ensure the DAO method was never called.
        verify(reportDAO, never()).getTopCustomersReport(anyString(), anyString(), anyInt());
    }

    /**
     * Test case for when an exception occurs in the DAO layer.
     * The controller should return a 500 Internal Server Error.
     */
    @Test
    void testGetReport_SQLException() throws SQLException {
        // Configure the mock DAO to throw a SQLException.
        when(reportDAO.getTopCustomersReport(anyString(), anyString(), anyInt())).thenThrow(new SQLException("Simulated DB error"));

        // Call the controller method.
        Response response = reportController.getTopCustomersReport("2024-01-01", "2024-12-31", 10);

        // Verify that the response status is 500 Internal Server Error.
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}
