//package Test;
//
//import com.google.gson.JsonObject;
//import dao.UserDAO;
//import dao.UserLoginDAO;
//import model.User;
//import service.UserLoginService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//public class UserLoginTest {
//
//    @Mock
//    private UserLoginDAO userLoginDAO;
//
//    @Mock
//    private UserDAO userDAO;
//
//    @InjectMocks
//    private UserLoginService userLoginService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//    
//    // Test case for a successful login where the password has been changed
//    @Test
//    public void testValidateUser_Success_PasswordChanged() {
//        // 1. Arrange: Set up the mock data and behavior
//        String username = "testuser";
//        String password = "testpassword";
//        
//        // Use the service's hashing method to generate the correct hash for comparison
//        String correctHash = userLoginService.hashPassword(password);
//        
//        // Mock the UserLoginDAO to return the correct password details
//        when(userLoginDAO.getPasswordDetailsByUsername(username))
//            .thenReturn(new String[]{correctHash, "1", "true"});
//
//        // Mock the UserDAO to return a User object
//        User mockUser = new User();
//        mockUser.setFirstName("Test");
//        mockUser.setRoleID(2);
//        when(userDAO.getUserDetails(1)).thenReturn(mockUser);
//
//        // 2. Act: Call the method being tested
//        JsonObject response = userLoginService.validateUser(username, password);
//
//        // 3. Assert: Verify the results
//        assertEquals(true, response.get("success").getAsBoolean(), "Login should be successful");
//        assertEquals("Login successful.", response.get("message").getAsString(), "Message should be correct");
//        assertEquals("Test", response.get("firstName").getAsString(), "First name should match");
//        assertEquals(2, response.get("roleID").getAsInt(), "Role ID should match");
//        assertEquals(true, response.get("passwordChanged").getAsBoolean(), "passwordChanged should be true");
//    }
//    
//    // Test case for a failed login with an incorrect password
//    @Test
//    public void testValidateUser_Failure_InvalidPassword() {
//        // 1. Arrange: Set up the mock data and behavior
//        String username = "testuser";
//        String correctPassword = "testpassword";
//        String wrongPassword = "wrongpassword";
//        
//        // Mock the DAO to return the hash of the correct password
//        String correctHash = userLoginService.hashPassword(correctPassword);
//        when(userLoginDAO.getPasswordDetailsByUsername(username))
//            .thenReturn(new String[]{correctHash, "1", "true"});
//
//        // 2. Act: Call the method with the incorrect password
//        JsonObject response = userLoginService.validateUser(username, wrongPassword);
//
//        // 3. Assert: Verify the results
//        assertEquals(false, response.get("success").getAsBoolean(), "Login should fail");
//        assertEquals("Invalid username or password.", response.get("message").getAsString(), "Message should indicate failure");
//    }
//    
//    // Test case for a new user's initial login, requiring a password change
//    @Test
//    public void testValidateUser_InitialLogin_PasswordNotChanged() {
//        // 1. Arrange: Set up the mock data for an initial login
//        String username = "newuser";
//        String password = "newuser@123";
//        
//        // Mock the DAO to return a user with passwordChanged = false
//        String correctHash = userLoginService.hashPassword(password);
//        when(userLoginDAO.getPasswordDetailsByUsername(username))
//            .thenReturn(new String[]{correctHash, "2", "false"});
//
//        // 2. Act: Call the method
//        JsonObject response = userLoginService.validateUser(username, password);
//        
//        // 3. Assert: Verify the results
//        assertEquals(true, response.get("success").getAsBoolean(), "Initial login should be successful");
//        assertEquals("Initial login detected. Please change your password.", response.get("message").getAsString(), "Message should prompt password change");
//        assertEquals(false, response.get("passwordChanged").getAsBoolean(), "passwordChanged should be false");
//        assertEquals(2, response.get("userID").getAsInt(), "User ID should be returned");
//    }
//    
//    // Test case for a non-existent user
//    @Test
//    public void testValidateUser_UserNotFound() {
//        // 1. Arrange: Set up the mock DAO to return no user data (null)
//        String username = "nonexistentuser";
//        String password = "somepassword";
//        
//        when(userLoginDAO.getPasswordDetailsByUsername(username)).thenReturn(null);
//
//        // 2. Act: Call the method with a non-existent username
//        JsonObject response = userLoginService.validateUser(username, password);
//
//        // 3. Assert: Verify the results
//        assertEquals(false, response.get("success").getAsBoolean(), "Login should fail");
//        assertEquals("Invalid username or password.", response.get("message").getAsString(), "Message should indicate failure");
//    }
//    
//    // Test to ensure the hashPassword method works as expected
//    @Test
//    public void testHashPassword() {
//        String password = "testpassword";
//        String expectedHash = "WfFjW9xM8h549qF/y6Xo9k66e852qYtW2d6yWqR/E1Y="; 
//        
//        String actualHash = userLoginService.hashPassword(password);
//        
//        assertEquals(expectedHash, actualHash, "Generated hash should match the expected hash");
//    }
//}