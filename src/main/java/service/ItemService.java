package service;

import dao.ItemDAO;
import model.Item;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.OrderItem;

/**
 * Service class for handling item-related business logic.
 * It acts as an intermediary between the controller and the DAO layer.
 */
public class ItemService {

    private final ItemDAO itemDAO = new ItemDAO();

    /**
     * Retrieves all items.
     * @return A list of all Item objects.
     */
    public List<Item> getAllItems() {
        return itemDAO.getAllItems();
    }

    /**
     * Retrieves a single item by its ID.
     * @param itemID The ID of the item.
     * @return The Item object, or null if not found.
     */
    public Item getItemById(int itemID) {
        return itemDAO.getItemById(itemID);
    }
    
    /**
     * Searches for items based on various criteria.
     * @param description A search string for the item description.
     * @param categoryID An integer for the category ID.
     * @param minPrice A minimum unit price.
     * @param maxPrice A maximum unit price.
     * @return A list of matching Item objects.
     */
    public List<Item> searchItems(String description, Integer categoryID, BigDecimal minPrice, BigDecimal maxPrice) {
        return itemDAO.searchItems(description, categoryID, minPrice, maxPrice);
    }

    /**
     * Adds a new item to the database.
     * @param item The Item object to add.
     * @return true if the item was added successfully, false otherwise.
     */
    public boolean addItem(Item item) {
        return itemDAO.insertItem(item);
    }

    /**
     * Updates an existing item.
     * @param item The Item object with updated details.
     * @return true if the item was updated successfully, false otherwise.
     */
    public boolean updateItem(Item item) {
        return itemDAO.updateItem(item);
    }
    
    /**
     * Deletes an item by its ID.
     * @param itemID The ID of the item to delete.
     * @return true if the item was deleted successfully, false otherwise.
     */
    public boolean deleteItem(int itemID) {
        return itemDAO.deleteItem(itemID);
    }

    BigDecimal getPriceByItemID(int itemID, Connection conn) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public boolean updateItemQuantities(Connection conn, List<OrderItem> orderItems) throws SQLException {
        for (OrderItem item : orderItems) {
            boolean updated = itemDAO.updateItemQuantity(conn, item.getItemID(), item.getQuantity());
            if (!updated) {
                // If any item fails to update, return false immediately
                return false;
            }
        }
        return true;
    }
}
