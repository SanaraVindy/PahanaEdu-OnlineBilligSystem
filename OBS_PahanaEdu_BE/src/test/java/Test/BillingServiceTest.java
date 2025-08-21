package Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import service.OrderService; // Import the OrderService
import config.DBConnection;

/**
 * This test class verifies the behavior of the BillingService. It uses Mockito
 * to isolate the service logic from the database, ensuring that the tests focus
 * on the business logic itself.
 */
public class BillingServiceTest {

    // Mock objects for the DAO layer dependencies
    private OrderDAO mockOrderDAO;
    private OrderItemDAO mockOrderItemDAO;
    private InvoiceDAO mockInvoiceDAO;
    private ItemDAO mockItemDAO;
    private Connection mockConnection;
    private OrderService mockOrderService; // Mock the OrderService

    // The service class to be tested
    private BillingService billingService;

    // Test data for the mocks
    private Order mockOrder;
    private List<OrderItem> mockOrderItems;

    @BeforeEach
    public void setUp() throws SQLException {
        // Initialize mock objects before each test
        mockOrderDAO = mock(OrderDAO.class);
        mockOrderItemDAO = mock(OrderItemDAO.class);
        mockInvoiceDAO = mock(InvoiceDAO.class);
        mockItemDAO = mock(ItemDAO.class);
        mockConnection = mock(Connection.class);
        mockOrderService = mock(OrderService.class); // Initialize the mock for OrderService

        // Instantiate the service with the new OrderService mock
        billingService = new BillingService(mockOrderService, mockOrderItemDAO, mockInvoiceDAO, mockItemDAO);

        // Set up common mock data
        mockOrder = new Order(); // Use the default constructor
        mockOrder.setCustomerID(1);

        mockOrderItems = new ArrayList<>();
        mockOrderItems.add(new OrderItem(1, 1));
        mockOrderItems.add(new OrderItem(2, 2));
    }

    /**
     * Test case to ensure a successful transaction with a discount.
     */
    @Test
    public void testProcessTransaction_Successful_Discount_BothRules() throws SQLException {
        // Arrange
        Item item1 = new Item();
        item1.setItemID(1);
        item1.setUnitPrice(new BigDecimal("3000.00"));
        item1.setCategoryID(7); // Children's books category
        
        Item item2 = new Item();
        item2.setItemID(2);
        item2.setUnitPrice(new BigDecimal("2000.00"));
        item2.setCategoryID(1);

        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).commit();
            
            when(mockItemDAO.getItem(eq(1), any(Connection.class))).thenReturn(item1);
            when(mockItemDAO.getItem(eq(2), any(Connection.class))).thenReturn(item2);
            
            int newOrderID = 101;
            when(mockOrderService.createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class)))
                .thenReturn(newOrderID);
            
            when(mockOrderItemDAO.addOrderItems(anyList(), any(Connection.class))).thenReturn(true);

            // Corrected: mock InvoiceDAO to return a generated ID (int) instead of a boolean
            when(mockInvoiceDAO.createInvoice(any(Connection.class), any(Invoice.class))).thenReturn(201);

            String paymentType = "Credit Card";

            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, paymentType);
            
            // Assert
            assertEquals("Transaction successful", result);

            verify(mockOrderService).createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class));
            verify(mockOrderItemDAO).addOrderItems(anyList(), any(Connection.class));

            ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
            verify(mockInvoiceDAO).createInvoice(any(Connection.class), invoiceCaptor.capture());
            
            Invoice capturedInvoice = invoiceCaptor.getValue();
            
            BigDecimal subtotal = (new BigDecimal("3000.00").multiply(BigDecimal.valueOf(1)))
                                     .add(new BigDecimal("2000.00").multiply(BigDecimal.valueOf(2)));
            
            BigDecimal discount10Percent = subtotal.multiply(new BigDecimal("0.10"));
            BigDecimal discount5Percent = subtotal.multiply(new BigDecimal("0.05"));
            
            BigDecimal expectedDiscount = discount10Percent.add(discount5Percent);
            BigDecimal expectedFinalTotal = subtotal.subtract(expectedDiscount);

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
        // Arrange
        Item item1 = new Item();
        item1.setUnitPrice(new BigDecimal("100.00"));

        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);

            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).rollback();

            when(mockItemDAO.getItem(anyInt(), any(Connection.class))).thenReturn(item1);

            when(mockOrderService.createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class)))
                .thenReturn(-1);

            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, "Cash");

            // Assert
            assertNotNull(result);
            assertEquals("Error: Failed to add order.", result, "Expected an error message for failed order creation.");

            // Verify that subsequent DAO methods were not called due to the early failure.
            verify(mockOrderService).createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class));
            verify(mockOrderItemDAO, never()).addOrderItems(anyList(), any(Connection.class));
            verify(mockInvoiceDAO, never()).createInvoice(any(Connection.class), any(Invoice.class));
        }
    }

    /**
     * Test case to ensure the method handles a null or empty list of order
     * items correctly.
     */
    @Test
    public void testProcessTransaction_NullOrEmptyOrderItems() throws SQLException {
        // Act with a null list of order items
        String resultNull = billingService.processTransaction(mockOrder, null, "Cash");
        assertNotNull(resultNull);
        assertEquals("Error: Order items list is null or empty.", resultNull, "Expected an error for null order items list.");

        // Act with an empty list of order items
        String resultEmpty = billingService.processTransaction(mockOrder, Collections.emptyList(), "Cash");
        assertNotNull(resultEmpty);
        assertEquals("Error: Order items list is null or empty.", resultEmpty, "Expected an error for empty order items list.");

        // Verify that no DAO methods were called
        verify(mockOrderService, never()).createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class));
        verify(mockOrderItemDAO, never()).addOrderItems(anyList(), any(Connection.class));
        verify(mockInvoiceDAO, never()).createInvoice(any(Connection.class), any(Invoice.class));
    }
}