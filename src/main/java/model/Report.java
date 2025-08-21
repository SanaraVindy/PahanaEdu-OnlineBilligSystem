package model;

public class Report {
    // Member variables for the report data.
    private int rank;
    private String customerName;
    private String firstName;
    private String lastName;
    private String email;
    private int totalOrders;
    private double totalSpent;
    private int loyaltyPoints;
    private String topCategory;

    /**
     * Default constructor. Required by some frameworks (like Gson) for deserialization.
     */
    public Report() {
        // Default constructor
    }

    /**
     * A comprehensive constructor for creating a full report object.
     * This is the preferred way to instantiate a Report object with all its properties.
     *
     * @param rank The customer's rank in the report.
     * @param firstName The customer's first name.
     * @param lastName The customer's last name.
     * @param email The customer's email address.
     * @param totalOrders The total number of orders placed by the customer.
     * @param totalSpent The total amount spent by the customer.
     * @param loyaltyPoints The customer's loyalty points.
     * @param topCategory The top category of items purchased by the customer.
     */
    public Report(int rank, String firstName, String lastName, String email, int totalOrders, double totalSpent, int loyaltyPoints, String topCategory) {
        this.rank = rank;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.totalOrders = totalOrders;
        this.totalSpent = totalSpent;
        this.loyaltyPoints = loyaltyPoints;
        this.topCategory = topCategory;
        // Combining first and last names for the customerName field.
        // NOTE: This approach is good because the customerName is derived from the other fields.
        // However, a better approach might be to not have a setter for customerName,
        // and instead have the getter compute it on the fly.
        this.customerName = firstName + " " + lastName;
    }

    /**
     * A simpler constructor, primarily for backward compatibility with tests.
     *
     * @param customerName The customer's full name.
     * @param totalRevenue The total revenue from the customer (totalSpent).
     * @param purchaseCount The number of purchases (totalOrders).
     */
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
    
    // Setter for customerName. This setter, along with the `updateCustomerName` logic,
    // can lead to inconsistencies if not managed carefully.
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        // Update customerName when firstName changes.
        updateCustomerName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        // Update customerName when lastName changes.
        updateCustomerName();
    }
    
    /**
     * Helper method to update customerName based on first and last names.
     */
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
    
    /**
     * This getter is for backward-compatibility with test files.
     * It's good practice to provide aliases if the underlying field name changes.
     */
    public double getTotalRevenue() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    /**
     * This getter is for backward-compatibility with test files.
     */
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