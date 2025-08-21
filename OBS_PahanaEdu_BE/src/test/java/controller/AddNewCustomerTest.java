package controller;

import controller.CustomerController;
import model.Customer;
import service.CustomerService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit 5 test class to specifically test the successful "Add New Customer" scenario.
 * It uses Mockito to simulate a valid HTTP POST request and verify the controller's behavior.
 */
public class AddNewCustomerTest {

    // InjectMocks injects mock objects into the controller instance.
    @InjectMocks
    private CustomerController customerController;

    // Mock annotations create mock instances of the servlet's dependencies.
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private CustomerService mockCustomerService;
    @Mock
    private PrintWriter mockPrintWriter;

    private final Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize the mocks created above and inject them into customerController.
        MockitoAnnotations.openMocks(this);
        // Mock the response's writer to capture what the servlet writes back.
        when(mockResponse.getWriter()).thenReturn(mockPrintWriter);
    }

    @Test
    public void testAddNewCustomer_SuccessCase() throws IOException {
        // --- GIVEN ---
        // 1. Prepare the JSON payload for a new customer based on the provided scenario.
        String newCustomerJson = "{\"firstName\":\"Test1\",\"lastName\":\"Test1\",\"email\":\"t1@gmail.com\",\"contactNo\":\"0712233456\"}";
        
        // 2. Create a mock BufferedReader to simulate the request body.
        BufferedReader mockReader = new BufferedReader(new StringReader(newCustomerJson));
        
        // 3. Configure the mock request to return our mock reader.
        when(mockRequest.getReader()).thenReturn(mockReader);

        // 4. Configure the mock CustomerService to return true, simulating a successful database insertion.
        // We use an ArgumentMatcher (any(Customer.class)) because the controller creates the Customer object,
        // so we don't have a direct reference to it.
        when(mockCustomerService.addCustomer(any(Customer.class))).thenReturn(true);

        // --- WHEN ---
        // 5. Call the doPost method of the controller, simulating the HTTP request.
        customerController.doPost(mockRequest, mockResponse);

        // --- THEN ---
        // 6. Verify that the response's content type was set correctly.
        verify(mockResponse).setContentType("application/json");

        // 7. Verify that the response status was set to 201 (SC_CREATED).
        verify(mockResponse).setStatus(HttpServletResponse.SC_CREATED);
        
        // 8. Verify that the service's addCustomer method was called exactly once with a non-null Customer object.
        verify(mockCustomerService, times(1)).addCustomer(any(Customer.class));
        
        // 9. Verify that the correct success message was written to the response writer.
        String expectedMessage = gson.toJson(Collections.singletonMap("message", "Customer added successfully"));
        verify(mockPrintWriter, times(1)).write(expectedMessage);
    }
}
