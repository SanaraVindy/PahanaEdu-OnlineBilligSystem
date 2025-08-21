package model;

public class Report {
    private int rank;
    private String customerName;
    private String firstName;
    private String lastName;
    private String email;
    private int totalOrders;
    private double totalSpent;
    private int loyaltyPoints;
    private String topCategory;

    public Report() {
        // Default constructor
    }

    // A comprehensive constructor for creating a full report object
    public Report(int rank, String firstName, String lastName, String email, int totalOrders, double totalSpent, int loyaltyPoints, String topCategory) {
        this.rank = rank;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.loyaltyPoints = loyaltyPoints;
        this.topCategory = topCategory;
        // Combining first and last names for the customerName field
        this.customerName = firstName + " " + lastName;
    }

    // A simpler constructor, primarily for backward compatibility with the tests
    public Report(String customerName, double totalRevenue, int purchaseCount) {
        this.customerName = customerName;
        this.totalSpent = totalRevenue;
        this.totalOrders = purchaseCount;
    }

    // Getters and Setters for all fields

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getCustomerName() {
        return customerName;
    }
    
    // Setter for customerName, useful if you want to set it directly
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        // Update customerName when firstName changes
        updateCustomerName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        // Update customerName when lastName changes
        updateCustomerName();
    }
    
    // Helper method to update customerName
    private void updateCustomerName() {
        if (this.firstName != null && this.lastName != null) {
            this.customerName = this.firstName + " " + this.lastName;
        } else {
            this.customerName = null;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public double getTotalSpent() {
        return totalSpent;
    }
    
    // This getter is for the backward-compatibility in the test file
    public double getTotalRevenue() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public int getPurchaseCount() {
        return totalOrders;
    }

    public void setPurchaseCount(int purchaseCount) {
        this.totalOrders = purchaseCount;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public void setTopCategory(String topCategory) {
        this.topCategory = topCategory;
    }
}
