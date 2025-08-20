package Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import dao.InvoiceDAO;
import dao.ItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import java.util.List;
import model.Invoice;
import model.Item;
import model.Order;
import model.OrderItem;
import service.BillingService;
import config.DBConnection;

/**
 * Unit tests for the BillingService class.
 * This class uses Mockito to isolate and test the business logic of the service.
 */
public class AddItemToBillTest {

    // Mock objects for the DAO layer dependencies
    private OrderDAO mockOrderDAO;
    private OrderItemDAO mockOrderItemDAO;
    private InvoiceDAO mockInvoiceDAO;
    private ItemDAO mockItemDAO;
    private Connection mockConnection;

    // The service class to be tested
    private BillingService billingService;

    @BeforeEach
    public void setUp() {
        // Initialize mock objects before each test
        mockOrderDAO = mock(OrderDAO.class);
        mockOrderItemDAO = mock(OrderItemDAO.class);
        mockInvoiceDAO = mock(InvoiceDAO.class);
        mockItemDAO = mock(ItemDAO.class);
        mockConnection = mock(Connection.class);

        // Instantiate the service with all required mock DAOs
        billingService = new BillingService(mockOrderDAO, mockOrderItemDAO, mockInvoiceDAO, mockItemDAO);
    }

    /**
     * Test case to ensure a transaction is processed successfully.
     * Mocks all DAO calls to simulate a successful database interaction.
     */
    @Test
    public void testProcessTransaction_Successful() throws SQLException {
        // Arrange
        int customerID = 1;
        int itemID = 101;
        int quantity = 2;
        BigDecimal unitPrice = new BigDecimal("50.00");
        BigDecimal grandTotal = unitPrice.multiply(new BigDecimal(quantity));
        String paymentMethod = "Cash";

        // Create a mock Order and OrderItem list
        Order mockOrder = new Order();
        mockOrder.setCustomerID(customerID);
        mockOrder.setTotalAmount(grandTotal);
        mockOrder.setOrderDate(new Date());

        List<OrderItem> mockOrderItems = Collections.singletonList(new OrderItem(itemID, quantity));

        // Use mockStatic to mock the static DbConnection.getConnection() method
        // This is the key fix for the "Communications link failure" error
        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            // Configure the mock static method to return our mock connection
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            // Set up a mock for the connection's auto-commit behavior
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).commit();
            
            // Mock DAO behaviors for a successful transaction
            when(mockOrderDAO.createOrder(any(Order.class), any(Connection.class))).thenReturn(1);
            when(mockOrderItemDAO.addOrderItems(anyList(), any(Connection.class))).thenReturn(true);
            when(mockInvoiceDAO.createInvoice(any(Connection.class), any(Invoice.class))).thenReturn(true);
            when(mockItemDAO.getItem(eq(itemID), any(Connection.class))).thenReturn(new Item());
    
            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, grandTotal, paymentMethod);
    
            // Assert
            assertEquals("Transaction successful", result);
    
            // Verify that the DAO methods were called correctly
            verify(mockOrderDAO).createOrder(any(Order.class), any(Connection.class));
            verify(mockOrderItemDAO).addOrderItems(anyList(), any(Connection.class));
            verify(mockInvoiceDAO).createInvoice(any(Connection.class), any(Invoice.class));
        }
    }

    /**
     * Test case for a transaction failure when the order cannot be created.
     */
    @Test
    public void testProcessTransaction_OrderCreationFailed() throws SQLException {
        // Arrange
        int customerID = 1;
        int itemID = 101;
        int quantity = 2;
        BigDecimal grandTotal = new BigDecimal("100.00");
        String paymentMethod = "Credit Card";
        
        Order mockOrder = new Order();
        mockOrder.setCustomerID(customerID);
        mockOrder.setTotalAmount(grandTotal);
        mockOrder.setOrderDate(new Date());
        List<OrderItem> mockOrderItems = Collections.singletonList(new OrderItem(itemID, quantity));

        // Use mockStatic to mock the static DbConnection.getConnection() method
        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            // Configure the mock static method to return our mock connection
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            // Set up a mock for the connection's auto-commit and rollback behavior
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).rollback();
            
            // Mock DAO behavior for a failed order creation
            when(mockOrderDAO.createOrder(any(Order.class), any(Connection.class))).thenReturn(-1);
            when(mockItemDAO.getItem(eq(itemID), any(Connection.class))).thenReturn(new Item());

            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, grandTotal, paymentMethod);
    
            // Assert
            assertEquals("Error: Failed to add order.", result);
            verify(mockOrderDAO).createOrder(any(Order.class), any(Connection.class));
            // Verify that subsequent methods were not called
            verify(mockOrderItemDAO, never()).addOrderItems(anyList(), any(Connection.class));
            verify(mockInvoiceDAO, never()).createInvoice(any(Connection.class), any(Invoice.class));
        }
    }
    
    /**
     * Test case for a transaction failure when order items cannot be added.
     */
    @Test
    public void testProcessTransaction_OrderItemsCreationFailed() throws SQLException {
        // Arrange
        int customerID = 1;
        int itemID = 101;
        int quantity = 2;
        BigDecimal grandTotal = new BigDecimal("100.00");
        String paymentMethod = "Credit Card";
        
        Order mockOrder = new Order();
        mockOrder.setCustomerID(customerID);
        mockOrder.setTotalAmount(grandTotal);
        mockOrder.setOrderDate(new Date());
        List<OrderItem> mockOrderItems = Collections.singletonList(new OrderItem(itemID, quantity));
        
        // Use mockStatic to mock the static DbConnection.getConnection() method
        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            // Set up a mock for the connection's auto-commit and rollback behavior
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).rollback();

            when(mockOrderDAO.createOrder(any(Order.class), any(Connection.class))).thenReturn(1);
            when(mockOrderItemDAO.addOrderItems(anyList(), any(Connection.class))).thenReturn(false);
            when(mockItemDAO.getItem(eq(itemID), any(Connection.class))).thenReturn(new Item());
    
            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, grandTotal, paymentMethod);
    
            // Assert
            assertEquals("Error: Failed to add order items.", result);
            verify(mockOrderDAO).createOrder(any(Order.class), any(Connection.class));
            verify(mockOrderItemDAO).addOrderItems(anyList(), any(Connection.class));
            verify(mockInvoiceDAO, never()).createInvoice(any(Connection.class), any(Invoice.class));
        }
    }

    /**
     * Test case for a transaction failure when the invoice cannot be created.
     */
    @Test
    public void testProcessTransaction_InvoiceCreationFailed() throws SQLException {
        // Arrange
        int customerID = 1;
        int itemID = 101;
        int quantity = 2;
        BigDecimal grandTotal = new BigDecimal("100.00");
        String paymentMethod = "Credit Card";
        
        Order mockOrder = new Order();
        mockOrder.setCustomerID(customerID);
        mockOrder.setTotalAmount(grandTotal);
        mockOrder.setOrderDate(new Date());
        List<OrderItem> mockOrderItems = Collections.singletonList(new OrderItem(itemID, quantity));

        // Use mockStatic to mock the static DbConnection.getConnection() method
        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            // Set up a mock for the connection's auto-commit and rollback behavior
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).rollback();

            when(mockOrderDAO.createOrder(any(Order.class), any(Connection.class))).thenReturn(1);
            when(mockOrderItemDAO.addOrderItems(anyList(), any(Connection.class))).thenReturn(true);
            when(mockInvoiceDAO.createInvoice(any(Connection.class), any(Invoice.class))).thenReturn(false);
            when(mockItemDAO.getItem(eq(itemID), any(Connection.class))).thenReturn(new Item());
    
            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, grandTotal, paymentMethod);
    
            // Assert
            assertEquals("Error: Failed to add invoice.", result);
            verify(mockOrderDAO).createOrder(any(Order.class), any(Connection.class));
            verify(mockOrderItemDAO).addOrderItems(anyList(), any(Connection.class));
            verify(mockInvoiceDAO).createInvoice(any(Connection.class), any(Invoice.class));
        }
    }

    /**
     * Test case to ensure a transaction is not processed with null or empty order items.
     */
    @Test
    public void testProcessTransaction_InvalidOrderItems() throws SQLException {
        // Act with null
        String resultNull = billingService.processTransaction(new Order(), null, new BigDecimal("0.00"), "Cash");
        assertEquals("Error: Order items list is null or empty.", resultNull);

        // Act with empty list
        String resultEmpty = billingService.processTransaction(new Order(), Collections.emptyList(), new BigDecimal("0.00"), "Cash");
        assertEquals("Error: Order items list is null or empty.", resultEmpty);

        // Verify that no DAO methods were called
        verify(mockOrderDAO, never()).createOrder(any(Order.class), any(Connection.class));
        verify(mockOrderItemDAO, never()).addOrderItems(anyList(), any(Connection.class));
        verify(mockInvoiceDAO, never()).createInvoice(any(Connection.class), any(Invoice.class));
    }
}
