package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The Order model class represents a customer order in the database.
 * It has fields corresponding to the columns in the `order` table.
 */
public class Order {

    private int orderID;
    private int customerID;
    private BigDecimal totalAmount;
    private Date orderDate;

    // Default constructor
    public Order() {
    }

    // Constructor for creating a new order
    public Order(int customerID, BigDecimal totalAmount, Date orderDate) {
        this.customerID = customerID;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }

    // Constructor to load an existing order from the database
    public Order(int orderID, int customerID, BigDecimal totalAmount, Date orderDate) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }

    // Getters and Setters
    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}