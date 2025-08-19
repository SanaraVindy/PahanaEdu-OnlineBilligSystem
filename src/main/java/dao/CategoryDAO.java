package dao;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Category;

public class CategoryDAO {

    public boolean addCategory(Category category) {
        String sql = "INSERT INTO category (type) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category.getType());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE category SET type = ? WHERE categoryID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category.getType());
            ps.setInt(2, category.getCategoryID());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategory(int categoryID) {
        String sql = "DELETE FROM category WHERE categoryID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Category getCategoryByID(int categoryID) {
        String sql = "SELECT * FROM category WHERE categoryID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setCategoryID(rs.getInt("categoryID"));
                    category.setType(rs.getString("type"));
                    return category;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryID(rs.getInt("categoryID"));
                category.setType(rs.getString("type"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<Category> searchCategories(String type) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE type LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + type + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setCategoryID(rs.getInt("categoryID"));
                    category.setType(rs.getString("type"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
}