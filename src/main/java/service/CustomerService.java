package service;

import dao.CustomerDAO;
import java.sql.Connection;
import java.sql.SQLException;
import model.Customer;

import java.util.List;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public boolean addCustomer(Customer customer) {
        return customerDAO.insertCustomer(customer);
    }

    public List<Customer> searchCustomers(String searchText, String mobile) {
        return customerDAO.searchCustomers(searchText, mobile);
    }

    public boolean deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId);
    }

    public boolean updateCustomer(Customer customer) {
        return customerDAO.updateCustomer(customer);
    }
        public boolean updateLoyaltyPoints(int customerID, int pointsToAdd, Connection conn) throws SQLException {
        return customerDAO.updateLoyaltyPoints(customerID, pointsToAdd, conn);
    }
}
