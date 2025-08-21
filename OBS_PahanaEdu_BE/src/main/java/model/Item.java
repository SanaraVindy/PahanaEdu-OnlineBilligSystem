package model;

import java.math.BigDecimal;

/**
 * The Item model class represents an item in the database. It has fields
 * corresponding to the columns in the `item` table.
 */
public class Item {

    private int itemID;
    private String description;
    private String identificationCode;
    private BigDecimal unitPrice; // Using BigDecimal for precise currency handling
    private int quantity;
    private int categoryID;

    // Default constructor
    public Item() {
    }

    // Constructor for creating a new item
    public Item(String description, String identificationCode, BigDecimal unitPrice, int quantity, int categoryID) {
        this.description = description;
        this.identificationCode = identificationCode;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.categoryID = categoryID;
    }

    // Constructor to load an existing item from the database
    public Item(int itemID, String description, String identificationCode, BigDecimal unitPrice, int quantity, int categoryID) {
        this.itemID = itemID;
        this.description = description;
        this.identificationCode = identificationCode;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.categoryID = categoryID;
    }

    // Getters and Setters
    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
    private String categoryType;

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

}
