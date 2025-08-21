package model;

import java.math.BigDecimal;
import java.util.Date;

public class Order {

    private int orderID;
    private int customerID;
    private Date orderDate;
    private BigDecimal totalAmount; // Renamed from grandTotal
    private BigDecimal discount;

    // Default constructor
    public Order() {
    }

    // Constructor to load an existing order from the database
    public Order(int orderID, int customerID, Date orderDate, BigDecimal totalAmount) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    // Corrected getter for the renamed field
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    // Corrected setter for the renamed field
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getDiscount() {
        return discount;
    }
    
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}