package service;

import java.util.List;
import dao.CategoryDAO;
import model.Category;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public boolean addCategory(Category category) {
        if (category == null || category.getType() == null || category.getType().trim().isEmpty()) {
            return false; // Basic validation
        }
        return categoryDAO.addCategory(category);
    }

    public boolean updateCategory(Category category) {
        if (category == null || category.getType() == null || category.getType().trim().isEmpty()) {
            return false; // Basic validation
        }
        return categoryDAO.updateCategory(category);
    }

    public boolean deleteCategory(int categoryID) {
        return categoryDAO.deleteCategory(categoryID);
    }

    public Category getCategoryByID(int categoryID) {
        return categoryDAO.getCategoryByID(categoryID);
    }

    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    public List<Category> searchCategories(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllCategories(); // Return all if search is empty
        }
        return categoryDAO.searchCategories(searchText);
    }
}