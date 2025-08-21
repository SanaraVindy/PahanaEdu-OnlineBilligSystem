package model;

/**
 * A model class to represent a monthly sales summary report.
 * This class is used to map database query results to a Java object.
 */
public class MonthlySalesSummary {
    private String month;
    private double totalRevenue;
    private int transactionCount;

    // A default, no-argument constructor is required by some frameworks,
    // like Gson, for object deserialization.
    public MonthlySalesSummary() {
    }

    // A constructor with arguments for convenience when creating objects programmatically.
    public MonthlySalesSummary(String month, double totalRevenue, int transactionCount) {
        this.month = month;
        this.totalRevenue = totalRevenue;
        this.transactionCount = transactionCount;
    }

    // Getters and setters for all fields.

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }
}
