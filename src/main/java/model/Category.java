package model;

public class Category {
    private int categoryID;
    private String type;

    public Category() {
    }

    public Category(int categoryID, String type) {
        this.categoryID = categoryID;
        this.type = type;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}