package model;

import java.math.BigDecimal;
import java.util.Date;

public class Invoice {
    private int invoiceID;
    private int orderID;
    private Date invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    // The field name has been updated to match the 'paymentType' column in your database.
    private String paymentType;

    public Invoice() {}

    public Invoice(int orderID, Date invoiceDate, BigDecimal totalAmount, BigDecimal discount, String paymentType) {
        this.orderID = orderID;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.paymentType = paymentType;
    }

    // Getters and Setters
    public int getInvoiceID() { return invoiceID; }
    public void setInvoiceID(int invoiceID) { this.invoiceID = invoiceID; }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    // The getter and setter have been updated to reflect the 'paymentType' field.
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
}
