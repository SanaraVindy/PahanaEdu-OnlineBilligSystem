package dao;

import config.DBConnection;
import model.Item;
import model.OrderItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Item entity.
 * This class handles all database operations related to the `item` table.
 */
public class ItemDAO {

    /**
     * Retrieves all items from the database.
     * @return A list of all Item objects.
     */
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        // SQL query to select all fields from the item table.
        String sql = "SELECT itemID, description, identificationCode, unitPrice, quantity, categoryID FROM pahanaedu.item;";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = new Item();
                item.setItemID(rs.getInt("itemID"));
                item.setDescription(rs.getString("description"));
                item.setIdentificationCode(rs.getString("identificationCode"));
                item.setUnitPrice(rs.getBigDecimal("unitPrice"));
                item.setQuantity(rs.getInt("quantity"));
                item.setCategoryID(rs.getInt("categoryID"));
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while fetching all items.");
            e.printStackTrace();
        }
        return items;
    }
    
    /**
     * Retrieves an item by its ID.
     * @param itemID The ID of the item to retrieve.
     * @return The Item object, or null if not found.
     */
    public Item getItemById(int itemID) {
        String sql = "SELECT itemID, description, identificationCode, unitPrice, quantity, categoryID FROM pahanaedu.item WHERE itemID = ?;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item();
                    item.setItemID(rs.getInt("itemID"));
                    item.setDescription(rs.getString("description"));
                    item.setIdentificationCode(rs.getString("identificationCode"));
                    item.setUnitPrice(rs.getBigDecimal("unitPrice"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCategoryID(rs.getInt("categoryID"));
                    return item;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred while fetching item by ID.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves an item by its ID using an existing database connection.
     * This method is intended for use within a transaction.
     * @param itemID The ID of the item to retrieve.
     * @param conn The database connection for the transaction.
     * @return The Item object, or null if not found.
     * @throws SQLException if a database access error occurs.
     */
    public Item getItem(int itemID, Connection conn) throws SQLException {
        String sql = "SELECT itemID, description, identificationCode, unitPrice, quantity, categoryID FROM pahanaedu.item WHERE itemID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Item item = new Item();
                    item.setItemID(rs.getInt("itemID"));
                    item.setDescription(rs.getString("description"));
                    item.setIdentificationCode(rs.getString("identificationCode"));
                    item.setUnitPrice(rs.getBigDecimal("unitPrice"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCategoryID(rs.getInt("categoryID"));
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Searches for items based on one or more criteria.
     * This method dynamically builds a query to handle various search combinations.
     * @param description A search string for the item description. Can be null or empty.
     * @param categoryID An integer for the category ID. Can be null.
     * @param minPrice A minimum price for the item. Can be null.
     * @param maxPrice A maximum price for the item. Can be null.
     * @return A list of Item objects that match the search criteria.
     */
    public List<Item> searchItems(String description, Integer categoryID, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Item> items = new ArrayList<>();
        // Start with a base SQL query that is always true (1=1) to simplify dynamic conditions.
        StringBuilder sqlBuilder = new StringBuilder("SELECT itemID, description, identificationCode, unitPrice, quantity, categoryID FROM pahanaedu.item WHERE 1=1");
        
        List<Object> params = new ArrayList<>();

        if (description != null && !description.trim().isEmpty()) {
            sqlBuilder.append(" AND description LIKE ?");
            params.add("%" + description + "%");
        }

        if (categoryID != null) {
            sqlBuilder.append(" AND categoryID = ?");
            params.add(categoryID);
        }

        if (minPrice != null) {
            sqlBuilder.append(" AND unitPrice >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sqlBuilder.append(" AND unitPrice <= ?");
            params.add(maxPrice);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            // Set parameters based on the dynamically built query.
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof BigDecimal) {
                    stmt.setBigDecimal(i + 1, (BigDecimal) param);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setItemID(rs.getInt("itemID"));
                    item.setDescription(rs.getString("description"));
                    item.setIdentificationCode(rs.getString("identificationCode"));
                    item.setUnitPrice(rs.getBigDecimal("unitPrice"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCategoryID(rs.getInt("categoryID"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error occurred during item search.");
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Inserts a new item into the database.
     * @param item The Item object to be inserted.
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean insertItem(Item item) {
        String sql = "INSERT INTO pahanaedu.item (description, identificationCode, unitPrice, quantity, categoryID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getDescription());
            stmt.setString(2, item.getIdentificationCode());
            stmt.setBigDecimal(3, item.getUnitPrice());
            stmt.setInt(4, item.getQuantity());
            stmt.setInt(5, item.getCategoryID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL error occurred while inserting a new item.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing item in the database.
     * @param item The Item object with updated information. The itemID is used to identify the record.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateItem(Item item) {
        String sql = "UPDATE pahanaedu.item SET description = ?, identificationCode = ?, unitPrice = ?, quantity = ?, categoryID = ? WHERE itemID = ?;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getDescription());
            stmt.setString(2, item.getIdentificationCode());
            stmt.setBigDecimal(3, item.getUnitPrice());
            stmt.setInt(4, item.getQuantity());
            stmt.setInt(5, item.getCategoryID());
            stmt.setInt(6, item.getItemID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL error occurred while updating the item.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an item from the database.
     * @param itemID The ID of the item to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    public boolean deleteItem(int itemID) {
        String sql = "DELETE FROM pahanaedu.item WHERE itemID = ?;";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemID);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL error occurred while deleting the item.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the quantity of a list of items by iterating and calling the
     * update method for each item. This method is used within a transaction.
     * @param orderItems The list of OrderItem objects containing the itemID and quantity to update.
     * @param conn The database connection for the transaction.
     * @return true if all updates are successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean updateItemQuantities(List<OrderItem> orderItems, Connection conn) throws SQLException {
        for (OrderItem item : orderItems) {
            if (!updateItemQuantity(item.getItemID(), item.getQuantity(), conn)) {
                return false; // Return false if any single update fails.
            }
        }
        return true;
    }

    /**
     * Updates the quantity of a single item in the database by subtracting the specified quantity.
     * This method ensures the quantity does not go negative and uses a shared connection.
     * @param itemID The ID of the item to update.
     * @param quantityToSubtract The quantity to subtract from the item's stock.
     * @param conn The database connection to use for the transaction.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    private boolean updateItemQuantity(int itemID, int quantityToSubtract, Connection conn) throws SQLException {
        String sql = "UPDATE pahanaedu.item SET quantity = quantity - ? WHERE itemID = ? AND quantity >= ?;";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantityToSubtract);
            stmt.setInt(2, itemID);
            stmt.setInt(3, quantityToSubtract); // Prevents quantity from going negative
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQL error occurred while updating the item quantity.");
            throw e; // Re-throw to allow the calling service to handle the transaction
        }
    }
}
