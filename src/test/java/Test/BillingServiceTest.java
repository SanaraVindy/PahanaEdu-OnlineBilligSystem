package Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.InvoiceDAO;
import dao.ItemDAO;
import model.Invoice;
import model.Item;
import model.Order;
import model.OrderItem;
import service.BillingService;
import config.DBConnection;

/**
 * This test class verifies the behavior of the BillingService.
 * It uses Mockito to isolate the service logic from the database,
 * ensuring that the tests focus on the business logic itself.
 */
public class BillingServiceTest {

    // Mock objects for the DAO layer dependencies
    private OrderDAO mockOrderDAO;
    private OrderItemDAO mockOrderItemDAO;
    private InvoiceDAO mockInvoiceDAO;
    private ItemDAO mockItemDAO;
    private Connection mockConnection;

    // The service class to be tested
    private BillingService billingService;

    // Test data for the mocks
    private Order mockOrder;
    private List<OrderItem> mockOrderItems;

    @BeforeEach
    public void setUp() {
        // Initialize mock objects before each test
        mockOrderDAO = mock(OrderDAO.class);
        mockOrderItemDAO = mock(OrderItemDAO.class);
        mockInvoiceDAO = mock(InvoiceDAO.class);
        mockItemDAO = mock(ItemDAO.class);
        mockConnection = mock(Connection.class);

        // Instantiate the service with the new ItemDAO mock
        billingService = new BillingService(mockOrderDAO, mockOrderItemDAO, mockInvoiceDAO, mockItemDAO);

        // Set up common mock data using the constructors
        mockOrder = new Order(1, new BigDecimal("100.00"), new java.sql.Date(System.currentTimeMillis()));
        mockOrderItems = new ArrayList<>();
        // Note: The OrderItem class now only has itemID and quantity
        mockOrderItems.add(new OrderItem(1, 1));
        mockOrderItems.add(new OrderItem(2, 2));
    }

    /**
     * Test case to ensure a successful transaction with a discount.
     */
    @Test
    public void testProcessTransaction_Successful_Discount_BothRules() throws SQLException {
        // Use mockStatic to mock the static DbConnection.getConnection() method
        // This is the key fix for the "Communications link failure" error
        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            // Configure the mock static method to return our mock connection
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            // Set up a mock for the connection's auto-commit and commit behavior
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).commit();
            
            // Mock the new createOrder method to return a generated ID
            int newOrderID = 101;
            when(mockOrderDAO.createOrder(any(Order.class), any(Connection.class))).thenReturn(newOrderID);
            
            // Mock the ItemDAO to return an item with categoryID 7 for the discount
            Item mockItemWithChildrenCategory = new Item();
            mockItemWithChildrenCategory.setCategoryID(7);
            when(mockItemDAO.getItem(eq(1), any(Connection.class))).thenReturn(mockItemWithChildrenCategory);
            when(mockItemDAO.getItem(eq(2), any(Connection.class))).thenReturn(new Item());
            
            // Mock the DAOs for a successful transaction.
            when(mockOrderItemDAO.addOrderItems(anyList(), any(Connection.class))).thenReturn(true);
            when(mockInvoiceDAO.createInvoice(any(Connection.class), any(Invoice.class))).thenReturn(true);

            // Define the transaction details
            BigDecimal totalAmount = new BigDecimal("7000.00"); // Matches first discount rule
            String paymentType = "Credit Card";

            String result = billingService.processTransaction(mockOrder, mockOrderItems, totalAmount, paymentType);
            
            // Verify the result
            assertEquals("Transaction successful", result);

            // Verify DAOs were called with the correct parameters
            verify(mockOrderDAO).createOrder(eq(mockOrder), any(Connection.class));
            verify(mockOrderItemDAO).addOrderItems(anyList(), any(Connection.class));

            // Use ArgumentCaptor to inspect the created Invoice object
            ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
            verify(mockInvoiceDAO).createInvoice(any(Connection.class), invoiceCaptor.capture());
            
            Invoice capturedInvoice = invoiceCaptor.getValue();
            
            // Expected discount is 10% (for 7000) + 5% (for category 7) of 7000 = 15%
            BigDecimal expectedDiscount = new BigDecimal("7000.00").multiply(new BigDecimal("0.15"));
            BigDecimal expectedFinalTotal = new BigDecimal("7000.00").subtract(expectedDiscount);

            // Assert that the invoice was created with the correct values
            assertEquals(newOrderID, capturedInvoice.getOrderID());
            assertEquals(expectedFinalTotal.stripTrailingZeros(), capturedInvoice.getTotalAmount().stripTrailingZeros());
            assertEquals(expectedDiscount.stripTrailingZeros(), capturedInvoice.getDiscount().stripTrailingZeros());
            assertEquals(paymentType, capturedInvoice.getPaymentType());
        }
    }

    /**
     * Test case for a transaction failure at the order creation stage.
     */
    @Test
    public void testProcessTransaction_OrderCreationFailed() throws SQLException {
        // Use mockStatic to mock the static DbConnection.getConnection() method
        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            // Set up a mock for the connection's auto-commit and rollback behavior
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).rollback();
            
            when(mockOrderDAO.createOrder(any(Order.class), any(Connection.class))).thenReturn(-1);

            String result = billingService.processTransaction(mockOrder, mockOrderItems, new BigDecimal("100.00"), "Cash");

            // Verify the result
            assertNotNull(result);
            assertEquals("Error: Failed to add order.", result, "Expected an error message for failed order creation.");
            
            // Verify that subsequent DAO methods were not called due to the early failure.
            verify(mockOrderItemDAO, never()).addOrderItems(anyList(), any(Connection.class));
            verify(mockInvoiceDAO, never()).createInvoice(any(Connection.class), any(Invoice.class));
        }
    }
    
    /**
     * Test case to ensure the method handles a null or empty list of order items correctly.
     */
    @Test
    public void testProcessTransaction_NullOrEmptyOrderItems() {
        // Test with a null list of order items
        String resultNull = billingService.processTransaction(mockOrder, null, new BigDecimal("100.00"), "Cash");
        assertNotNull(resultNull);
        assertEquals("Error: Order items list is null or empty.", resultNull, "Expected an error for null order items list.");

        // Test with an empty list of order items
        String resultEmpty = billingService.processTransaction(mockOrder, Collections.emptyList(), new BigDecimal("100.00"), "Cash");
        assertNotNull(resultEmpty);
        assertEquals("Error: Order items list is null or empty.", resultEmpty, "Expected an error for empty order items list.");
    }
}
