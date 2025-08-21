package controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dao.InvoiceDAO;
import dao.ItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import model.Order;
import model.OrderItem;
import service.BillingService;
import service.OrderService; // Import OrderService

@WebServlet("/api/billing")
public class BillingController extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    
    // Step 1: Instantiate OrderService correctly
    private final OrderService orderService = new OrderService(orderDAO);
    
    // Step 2: Instantiate BillingService with the correct dependencies
    private final BillingService billingService = new BillingService(orderService, orderItemDAO, invoiceDAO, itemDAO);
    
    private final Gson gson = new Gson();

    private static class BillingRequest {
        int customerID;
        List<OrderItem> cartItems;
        String paymentMethod;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            BillingRequest requestData = gson.fromJson(req.getReader(), BillingRequest.class);

            if (requestData == null || requestData.cartItems == null || requestData.cartItems.isEmpty() || requestData.customerID <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid request data. Customer ID and at least one item are required.\"}");
                return;
            }

            Order order = new Order();
            order.setCustomerID(requestData.customerID);
            order.setOrderDate(new Date());

            String result = billingService.processTransaction(order, requestData.cartItems, requestData.paymentMethod);
            
            if ("Transaction successful".equals(result)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\":\"Billing transaction completed successfully.\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"" + result + "\"}");
            }
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid JSON format in request body.\"}");
            e.printStackTrace();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"An unexpected server error occurred: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}