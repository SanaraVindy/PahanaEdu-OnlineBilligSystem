package model;

public class UserLogin {
    private int loginID;
    private int userID;
    private String username;
    private String password;
    private boolean passwordChanged;

    // Constructors
    public UserLogin() {
    }

    public UserLogin(int loginID, int userID, String username, String password, boolean passwordChanged) {
        this.loginID = loginID;
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.passwordChanged = passwordChanged;
    }

    // Getters and Setters
    public int getLoginID() {
        return loginID;
    }

    public void setLoginID(int loginID) {
        this.loginID = loginID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }
}