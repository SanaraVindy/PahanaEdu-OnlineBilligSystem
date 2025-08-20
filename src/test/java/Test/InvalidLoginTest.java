package Test;

import com.google.gson.JsonObject;
import dao.UserDAO;
import dao.UserLoginDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserLoginService;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * This test class verifies the behavior of the UserLoginService when
 * handling invalid login attempts, such as incorrect credentials or
 * non-existent users.
 *
 * It uses Mockito to mock the DAO layer, ensuring the tests focus
 * exclusively on the business logic within the UserLoginService.
 */
public class InvalidLoginTest {

    // Mock objects for the dependencies
    private UserLoginDAO mockUserLoginDAO;
    private UserDAO mockUserDAO;
    
    // The service class to be tested
    private UserLoginService userLoginService;
    
    // Test data
    private final String TEST_USERNAME = "testuser";
    private final String CORRECT_PASSWORD = "Password123";
    private final String WRONG_PASSWORD = "wrongpassword";
    private final String CORRECT_HASH = "Gq+lB4wH0rG2E+FjXn7vK6pSjN/jI2qWjY4rN9dG8kY="; // Hash for "Password123"

    @BeforeEach
    public void setUp() {
        // Initialize mock objects before each test
        mockUserLoginDAO = mock(UserLoginDAO.class);
        mockUserDAO = mock(UserDAO.class);
        
        // Instantiate the service with the mocked dependencies
        userLoginService = new UserLoginService(mockUserLoginDAO, mockUserDAO);
    }

    /**
     * Test case for logging in with an incorrect password.
     * The DAO will return valid user data, but the service should fail
     * the login because the hashed passwords do not match.
     */
    @Test
    public void testLoginWithIncorrectPassword() {
        // Mock the UserLoginDAO to return a user with a specific password hash
        when(mockUserLoginDAO.getPasswordDetailsByUsername(TEST_USERNAME))
              .thenReturn(new String[]{CORRECT_HASH, "1", "true"});
        
        // Call the method under test with the correct username and a wrong password
        JsonObject result = userLoginService.validateUser(TEST_USERNAME, WRONG_PASSWORD);
        
        // Verify the response is correct
        assertFalse(result.get("success").getAsBoolean(), 
            "The success flag should be false for an incorrect password.");
        assertEquals("Invalid username or password.", result.get("message").getAsString(),
            "The error message should indicate an invalid password.");
        
        // Verify that the DAO method was called and that the user details
        // were not retrieved from the UserDAO since login failed early
        verify(mockUserLoginDAO).getPasswordDetailsByUsername(TEST_USERNAME);
        verify(mockUserDAO, never()).getUserDetails(anyInt());
    }

    /**
     * Test case for logging in with a username that does not exist.
     * The DAO will return null, and the service should handle this gracefully.
     */
    @Test
    public void testLoginWithNonExistentUser() {
        // Mock the UserLoginDAO to return null, simulating a non-existent user
        when(mockUserLoginDAO.getPasswordDetailsByUsername("nonexistentuser"))
              .thenReturn(null);
        
        // Call the method under test
        JsonObject result = userLoginService.validateUser("nonexistentuser", "anypassword");
        
        // Verify the response is correct
        assertFalse(result.get("success").getAsBoolean(), 
            "The success flag should be false for a non-existent user.");
        assertEquals("Invalid username or password.", result.get("message").getAsString(),
            "The error message should be the same for non-existent users.");
            
        // Verify that the DAO was called
        verify(mockUserLoginDAO).getPasswordDetailsByUsername("nonexistentuser");
    }

    /**
     * Test case for a null or empty password input.
     * The service should handle this gracefully and return an error message.
     */
    @Test
    public void testLoginWithEmptyPassword() {
        // Mock the DAO to return user details.
        when(mockUserLoginDAO.getPasswordDetailsByUsername(TEST_USERNAME))
              .thenReturn(new String[]{CORRECT_HASH, "1", "true"});
              
        // Test with empty password
        JsonObject result = userLoginService.validateUser(TEST_USERNAME, "");
        assertFalse(result.get("success").getAsBoolean(), "Success flag should be false for empty password.");
        // Correct the expected message to match the updated business logic
        assertEquals("Username and password are required.", result.get("message").getAsString(), 
                     "Message should be for an empty password.");

        // Test with null password
        result = userLoginService.validateUser(TEST_USERNAME, null);
        assertFalse(result.get("success").getAsBoolean(), "Success flag should be false for null password.");
        // Correct the expected message to match the updated business logic
        assertEquals("Username and password are required.", result.get("message").getAsString(), 
                     "Message should be for a null password.");

        // Verify that the DAO was not called as the service returns early
        verify(mockUserLoginDAO, never()).getPasswordDetailsByUsername(anyString());
    }
}
