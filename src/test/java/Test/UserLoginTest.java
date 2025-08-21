package Test;

import com.google.gson.JsonObject;
import dao.UserDAO;
import dao.UserLoginDAO;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.UserLoginService;

import static org.mockito.Mockito.*;

/**
 * JUnit 5 test class for the UserLoginService.
 * This class uses Mockito to mock the DAO dependencies, allowing for isolated
 * testing of the service's business logic without a real database.
 * The provided template has been filled with comprehensive test cases.
 */
public class UserLoginTest {

    // @Mock creates a mock instance of the class
    @Mock
    private UserLoginDAO userLoginDAO;
    @Mock
    private UserDAO userDAO;

    // The service instance is now manually created in the setup method
    private UserLoginService userLoginService;

    // A sample password to use for testing
    private static final String TEST_PASSWORD = "testPassword123";

    private static final String TEST_USERNAME = "testuser";
    private static final int TEST_USER_ID = 101;
    private static final String TEST_FIRST_NAME = "Tester";
    private static final int TEST_ROLE_ID = 1;

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    /**
     * This method runs before each test. It initializes the Mockito mocks and
     * then manually injects them into the service class.
     */
    @BeforeEach
    public void setUp() {
        // Initialize the mocks created with the @Mock annotation
        MockitoAnnotations.openMocks(this);
        // Manually create the service instance, injecting the mocks
        userLoginService = new UserLoginService(userLoginDAO, userDAO);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test case for a successful login with a password that has already been changed.
     * This simulates a normal, successful login.
     */
    @Test
    public void testValidateUser_SuccessfulLogin() {
        // --- GIVEN ---
        // Dynamically get the hashed password from the service's own method
        String hashedPassword = userLoginService.hashPassword(TEST_PASSWORD);

        when(userLoginDAO.getPasswordDetailsByUsername(TEST_USERNAME))
                .thenReturn(new String[]{hashedPassword, String.valueOf(TEST_USER_ID), "true"});

        User mockUser = new User();
        mockUser.setUserID(TEST_USER_ID);
        mockUser.setFirstName(TEST_FIRST_NAME);
        mockUser.setRoleID(TEST_ROLE_ID);
        when(userDAO.getUserDetails(TEST_USER_ID)).thenReturn(mockUser);

        // --- WHEN ---
        JsonObject result = userLoginService.validateUser(TEST_USERNAME, TEST_PASSWORD);

        // --- THEN ---
        assertTrue(result.get("success").getAsBoolean(), "Login should be successful");
        assertEquals("Login successful.", result.get("message").getAsString(), "Message should indicate success");
        assertEquals(TEST_FIRST_NAME, result.get("firstName").getAsString(), "First name should match");
        assertEquals(TEST_ROLE_ID, result.get("roleID").getAsInt(), "Role ID should match");
        assertTrue(result.get("passwordChanged").getAsBoolean(), "passwordChanged flag should be true");

        verify(userLoginDAO, times(1)).getPasswordDetailsByUsername(TEST_USERNAME);
        verify(userDAO, times(1)).getUserDetails(TEST_USER_ID);
    }

    /**
     * Test case for a successful initial login, where the user is prompted to change their password.
     */
    @Test
    public void testValidateUser_InitialLogin_PasswordChangeNeeded() {
        // --- GIVEN ---
        // Dynamically get the hashed password from the service's own method
        String hashedPassword = userLoginService.hashPassword(TEST_PASSWORD);

        when(userLoginDAO.getPasswordDetailsByUsername(TEST_USERNAME))
                .thenReturn(new String[]{hashedPassword, String.valueOf(TEST_USER_ID), "false"});

        // --- WHEN ---
        JsonObject result = userLoginService.validateUser(TEST_USERNAME, TEST_PASSWORD);

        // --- THEN ---
        assertTrue(result.get("success").getAsBoolean(), "Login should be successful");
        assertEquals("Initial login detected. Please change your password.", result.get("message").getAsString(), "Message should prompt for password change");
        assertFalse(result.get("passwordChanged").getAsBoolean(), "passwordChanged flag should be false");
        assertEquals(TEST_USER_ID, result.get("userID").getAsInt(), "User ID should be returned");

        verify(userLoginDAO, times(1)).getPasswordDetailsByUsername(TEST_USERNAME);
        verify(userDAO, never()).getUserDetails(anyInt());
    }

    /**
     * Test case for a login attempt with an incorrect password.
     */
    @Test
    public void testValidateUser_InvalidPassword() {
        // --- GIVEN ---
        String hashedPassword = userLoginService.hashPassword(TEST_PASSWORD);

        when(userLoginDAO.getPasswordDetailsByUsername(TEST_USERNAME))
                .thenReturn(new String[]{hashedPassword, String.valueOf(TEST_USER_ID), "true"});

        // --- WHEN ---
        JsonObject result = userLoginService.validateUser(TEST_USERNAME, "wrongPassword");

        // --- THEN ---
        assertFalse(result.get("success").getAsBoolean(), "Login should fail with wrong password");
        assertEquals("Invalid username or password.", result.get("message").getAsString(), "Message should indicate failure");

        verify(userLoginDAO, times(1)).getPasswordDetailsByUsername(TEST_USERNAME);
        verify(userDAO, never()).getUserDetails(anyInt());
    }

    /**
     * Test case for a login attempt with a non-existent username.
     */
    @Test
    public void testValidateUser_UserNotFound() {
        // --- GIVEN ---
        when(userLoginDAO.getPasswordDetailsByUsername(anyString())).thenReturn(null);

        // --- WHEN ---
        JsonObject result = userLoginService.validateUser("nonexistentuser", TEST_PASSWORD);

        // --- THEN ---
        assertFalse(result.get("success").getAsBoolean(), "Login should fail for a non-existent user");
        assertEquals("Invalid username or password.", result.get("message").getAsString(), "Message should indicate failure");

        verify(userLoginDAO, times(1)).getPasswordDetailsByUsername("nonexistentuser");
        verify(userDAO, never()).getUserDetails(anyInt());
    }

    /**
     * Test case for a successful password change.
     */
    @Test
    public void testChangePassword_Successful() {
        // --- GIVEN ---
        when(userLoginDAO.updatePassword(eq(TEST_USER_ID), anyString())).thenReturn(true);

        // --- WHEN ---
        JsonObject result = userLoginService.changePassword(TEST_USER_ID, "newPassword123");

        // --- THEN ---
        assertTrue(result.get("success").getAsBoolean(), "Password change should be successful");
        assertEquals("Password changed successfully.", result.get("message").getAsString(), "Message should indicate success");

        verify(userLoginDAO, times(1)).updatePassword(eq(TEST_USER_ID), anyString());
    }

    /**
     * Test case for a failed password change.
     */
    @Test
    public void testChangePassword_Failed() {
        // --- GIVEN ---
        when(userLoginDAO.updatePassword(anyInt(), anyString())).thenReturn(false);

        // --- WHEN ---
        JsonObject result = userLoginService.changePassword(TEST_USER_ID, "newPassword123");

        // --- THEN ---
        assertFalse(result.get("success").getAsBoolean(), "Password change should fail");
        assertEquals("Failed to change password. Please try again.", result.get("message").getAsString(), "Message should indicate failure");

        verify(userLoginDAO, times(1)).updatePassword(anyInt(), anyString());
    }

    /**
     * Test case to verify that the hashPassword method returns a valid hash.
     * This is a simple test to ensure the hashing algorithm works as expected.
     */
    @Test
    public void testHashPassword_ReturnsValidHash() {
        // --- GIVEN ---
        String password = "testPassword";

        // --- WHEN ---
        String hashedPassword = userLoginService.hashPassword(password);

        // --- THEN ---
        assertNotNull(hashedPassword, "Hashed password should not be null");
        assertFalse(hashedPassword.isEmpty(), "Hashed password should not be empty");
        // Re-hash to ensure consistency
        assertEquals(userLoginService.hashPassword(password), hashedPassword, "Hashed password should be consistent");
    }
}
