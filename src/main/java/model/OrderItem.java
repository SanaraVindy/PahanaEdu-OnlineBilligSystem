package model;

import java.math.BigDecimal;

/**
 * The OrderItem model class represents a single item within an order.
 * It has fields corresponding to the columns in the `order_item` table.
 */
public class OrderItem {

    private int orderItemID;
    private int orderID;
    private int itemID;
    private int quantity;
    // The priceAtPurchase field has been removed to match the updated database schema.

    // Default constructor
    public OrderItem() {
    }

    /**
     * Constructor for creating a new order item.
     * @param orderID The ID of the order this item belongs to.
     * @param itemID The ID of the item being purchased.
     * @param quantity The number of units of the item.
     */
    public OrderItem(int orderID, int itemID, int quantity) {
        this.orderID = orderID;
        this.itemID = itemID;
        this.quantity = quantity;
    }

    /**
     * Constructor to load an existing order item from the database.
     * @param orderItemID The unique ID of the order item.
     * @param orderID The ID of the order this item belongs to.
     * @param itemID The ID of the item being purchased.
     * @param quantity The number of units of the item.
     */
    public OrderItem(int orderItemID, int orderID, int itemID, int quantity) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.itemID = itemID;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getOrderItemID() {
        return orderItemID;
    }

    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
