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
import service.OrderService; // Import OrderService
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
    private OrderService mockOrderService;

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

        // FIX: Correctly instantiate mockOrderService as a Mockito mock
        mockOrderService = mock(OrderService.class);

        // Instantiate the service with all required mock dependencies
        billingService = new BillingService(mockOrderService, mockOrderItemDAO, mockInvoiceDAO, mockItemDAO);
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
        String paymentMethod = "Cash";

        Order mockOrder = new Order();
        mockOrder.setCustomerID(customerID);
        mockOrder.setOrderDate(new Date());

        List<OrderItem> mockOrderItems = Collections.singletonList(new OrderItem(itemID, quantity));

        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).commit();
            
            Item mockItem = new Item();
            mockItem.setUnitPrice(unitPrice);
            mockItem.setCategoryID(1); // Not children's book category
            when(mockItemDAO.getItem(eq(itemID), any(Connection.class))).thenReturn(mockItem);
            
            // Correctly mock OrderService.createOrder, which returns int
            when(mockOrderService.createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class))).thenReturn(1);
            
            when(mockOrderItemDAO.addOrderItems(anyList(), any(Connection.class))).thenReturn(true);
            
            // Correctly mock InvoiceDAO to return an int
            when(mockInvoiceDAO.createInvoice(any(Connection.class), any(Invoice.class))).thenReturn(1);
    
            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, paymentMethod);
    
            // Assert
            assertEquals("Transaction successful", result);
    
            // Verify that the DAO methods were called correctly
            verify(mockOrderService).createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class));
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
        BigDecimal unitPrice = new BigDecimal("50.00");
        String paymentMethod = "Credit Card";
        
        Order mockOrder = new Order();
        mockOrder.setCustomerID(customerID);
        mockOrder.setOrderDate(new Date());
        List<OrderItem> mockOrderItems = Collections.singletonList(new OrderItem(itemID, quantity));

        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).rollback();
            
            Item mockItem = new Item();
            mockItem.setUnitPrice(unitPrice);
            when(mockItemDAO.getItem(eq(itemID), any(Connection.class))).thenReturn(mockItem);

            // Correctly mock OrderService.createOrder to return -1
            when(mockOrderService.createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class))).thenReturn(-1);

            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, paymentMethod);
    
            // Assert
            assertEquals("Error: Failed to add order.", result);
            
            // Verify that subsequent DAO methods were not called due to the early failure.
            verify(mockOrderService).createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class));
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
        BigDecimal unitPrice = new BigDecimal("50.00");
        String paymentMethod = "Credit Card";
        
        Order mockOrder = new Order();
        mockOrder.setCustomerID(customerID);
        mockOrder.setOrderDate(new Date());
        List<OrderItem> mockOrderItems = Collections.singletonList(new OrderItem(itemID, quantity));
        
        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).rollback();
            
            Item mockItem = new Item();
            mockItem.setUnitPrice(unitPrice);
            when(mockItemDAO.getItem(eq(itemID), any(Connection.class))).thenReturn(mockItem);

            when(mockOrderService.createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class))).thenReturn(1);
            when(mockOrderItemDAO.addOrderItems(anyList(), any(Connection.class))).thenReturn(false);
    
            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, paymentMethod);
    
            // Assert
            assertEquals("Error: Failed to add order items.", result);
            
            verify(mockOrderService).createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class));
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
        BigDecimal unitPrice = new BigDecimal("50.00");
        String paymentMethod = "Credit Card";
        
        Order mockOrder = new Order();
        mockOrder.setCustomerID(customerID);
        mockOrder.setOrderDate(new Date());
        List<OrderItem> mockOrderItems = Collections.singletonList(new OrderItem(itemID, quantity));

        try (MockedStatic<DBConnection> mockedDbConnection = mockStatic(DBConnection.class)) {
            mockedDbConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            
            doNothing().when(mockConnection).setAutoCommit(anyBoolean());
            doNothing().when(mockConnection).rollback();

            Item mockItem = new Item();
            mockItem.setUnitPrice(unitPrice);
            when(mockItemDAO.getItem(eq(itemID), any(Connection.class))).thenReturn(mockItem);
            
            when(mockOrderService.createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class))).thenReturn(1);
            when(mockOrderItemDAO.addOrderItems(anyList(), any(Connection.class))).thenReturn(true);
            
            // Correctly mock InvoiceDAO to return -1 for failure
            when(mockInvoiceDAO.createInvoice(any(Connection.class), any(Invoice.class))).thenReturn(-1);
    
            // Act
            String result = billingService.processTransaction(mockOrder, mockOrderItems, paymentMethod);
    
            // Assert
            assertEquals("Error: Failed to create invoice.", result);
            
            verify(mockOrderService).createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class));
            verify(mockOrderItemDAO).addOrderItems(anyList(), any(Connection.class));
            verify(mockInvoiceDAO).createInvoice(any(Connection.class), any(Invoice.class));
        }
    }

    /**
     * Test case to ensure a transaction is not processed with null or empty order items.
     */
    @Test
    public void testProcessTransaction_InvalidOrderItems() throws SQLException {
        // Arrange
        Order mockOrder = new Order();

        // Act with null
        String resultNull = billingService.processTransaction(mockOrder, null, "Cash");
        assertEquals("Error: Order items list is null or empty.", resultNull);

        // Act with empty list
        String resultEmpty = billingService.processTransaction(mockOrder, Collections.emptyList(), "Cash");
        assertEquals("Error: Order items list is null or empty.", resultEmpty);

        // Verify that no DAO methods were called
        verify(mockOrderService, never()).createOrder(any(Order.class), any(BigDecimal.class), any(BigDecimal.class), any(Connection.class));
        verify(mockOrderItemDAO, never()).addOrderItems(anyList(), any(Connection.class));
        verify(mockInvoiceDAO, never()).createInvoice(any(Connection.class), any(Invoice.class));
    }
}