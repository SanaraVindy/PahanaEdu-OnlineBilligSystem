package controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Customer;
import service.CustomerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/customers/*")
public class CustomerController extends HttpServlet {

    // Removed the 'final' keyword and initialization.
    private CustomerService customerService;
    private final Gson gson = new Gson();

    /**
     * Default constructor for servlet container initialization.
     * This ensures the servlet container can instantiate the class.
     */
    public CustomerController() {
        this(new CustomerService()); // Calls the parameterized constructor with a real service.
    }

    /**
     * Constructor for dependency injection, primarily used for testing.
     *
     * @param customerService A service instance to be used by the controller.
     */
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String searchText = request.getParameter("search");
        String mobile = request.getParameter("mobile");

        List<Customer> customers;
        if ((searchText != null && !searchText.trim().isEmpty()) || (mobile != null && !mobile.trim().isEmpty())) {
            customers = customerService.searchCustomers(searchText, mobile);
        } else {
            customers = customerService.getAllCustomers();
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(customers));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            // Read request body safely using a single stream operation.
            String jsonBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            // Parse JSON into Customer
            Customer customer = gson.fromJson(jsonBody, Customer.class);

            // Validate required fields
            if (customer == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(
                        Map.of("error", "Request body cannot be empty or invalid")
                ));
                return;
            }

            if (isNullOrEmpty(customer.getFirstName()) ||
                isNullOrEmpty(customer.getLastName()) ||
                isNullOrEmpty(customer.getEmail()) ||
                isNullOrEmpty(customer.getContactNo())) {

                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(
                        Map.of("error", "All fields except loyalty points are required.")
                ));
                return;
            }

            // Save to DB
            boolean isAdded = customerService.addCustomer(customer);
            if (isAdded) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(
                        Map.of("message", "Customer added successfully")
                ));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(
                        Map.of("error", "Database insertion failed")
                ));
            }

        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(
                    Map.of("error", "Invalid JSON format")
            ));
        } catch (Exception e) {
            // This now correctly catches the SQLIntegrityConstraintViolationException
            System.err.println("Server error: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(
                    Map.of("error", "Server error: " + e.getMessage())
            ));
        }
    }

    // Utility method for cleaner validation
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    // ... (doPut and doDelete methods remain unchanged)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo(); // e.g. "/5"
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Customer ID required in URL\"}");
            return;
        }

        int customerId = Integer.parseInt(pathInfo.substring(1));

        try (BufferedReader reader = req.getReader()) {
            Customer customer = gson.fromJson(reader, Customer.class);
            customer.setCustomerID(customerId);

            boolean updated = customerService.updateCustomer(customer);
            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"message\":\"Customer updated successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Customer not found\"}");
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo(); // e.g. "/5"
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Customer ID required in URL\"}");
            return;
        }

        int customerId = Integer.parseInt(pathInfo.substring(1));

        boolean deleted = customerService.deleteCustomer(customerId);
        if (deleted) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"message\":\"Customer deleted successfully\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Customer not found\"}");
        }
    }
}
