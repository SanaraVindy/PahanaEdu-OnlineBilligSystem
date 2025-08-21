package service;

import dao.ItemDAO;
import model.Item;
import model.OrderItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.math.BigDecimal;


/**
 * Service class for handling item-related business logic.
 * This layer acts as an intermediary between the Controller and DAO layers,
 * providing a place for business logic, validation, and transaction management.
 */
public class ItemService {
    private final ItemDAO itemDAO = new ItemDAO();

    /**
     * Retrieves all items from the database.
     * @return A list of all Item objects.
     */
    public List<Item> getAllItems() {
        return itemDAO.getAllItems();
    }

    /**
     * Retrieves a single item by its ID.
     * @param itemID The ID of the item to retrieve.
     * @return The Item object, or null if not found.
     */
    public Item getItemById(int itemID) {
        return itemDAO.getItemById(itemID);
    }

    /**
     * Searches for items based on a dynamic set of criteria.
     * @param description A search string for the item description.
     * @param categoryID An integer for the category ID.
     * @param minPrice A minimum price for the item.
     * @param maxPrice A maximum price for the item.
     * @return A list of matching Item objects.
     */
    public List<Item> searchItems(String description, Integer categoryID, BigDecimal minPrice, BigDecimal maxPrice) {
        return itemDAO.searchItems(description, categoryID, minPrice, maxPrice);
    }

    /**
     * Adds a new item to the database.
     * @param item The Item object to be inserted.
     * @return true if the item was added successfully, false otherwise.
     */
    public boolean addItem(Item item) {
        return itemDAO.insertItem(item);
    }

    /**
     * Updates an existing item in the database.
     * @param item The Item object with updated information.
     * @return true if the item was updated successfully, false otherwise.
     */
    public boolean updateItem(Item item) {
        return itemDAO.updateItem(item);
    }

    /**
     * Deletes an item from the database.
     * @param itemID The ID of the item to delete.
     * @return true if the item was deleted successfully, false otherwise.
     */
    public boolean deleteItem(int itemID) {
        return itemDAO.deleteItem(itemID);
    }

    /**
     * Updates the quantities of a list of items within a transaction.
     * It passes the database connection to the DAO to ensure all updates
     * are part of the same transaction. This method is the core business logic
     * for handling stock reduction during a sale.
     *
     * @param conn The database connection to use for the transaction.
     * @param orderItems The list of OrderItem objects containing the quantity to update.
     * @return true if the updates are successful, false otherwise.
     * @throws SQLException if a database access error occurs during the operation.
     */
    public boolean updateItemQuantities(Connection conn, List<OrderItem> orderItems) throws SQLException {
        // The service layer passes the connection to the DAO.
        // This ensures all item quantity updates are performed on the same connection,
        // which is crucial for transactional integrity.
        return itemDAO.updateItemQuantities(orderItems, conn);
    }
}
