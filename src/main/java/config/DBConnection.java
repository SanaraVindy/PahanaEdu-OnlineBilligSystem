package config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties props = new Properties();
            try (InputStream input = DBConnection.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                if (input == null) {
                    throw new SQLException("application.properties not found in classpath");
                }
                props.load(input);
            } catch (IOException e) {
                throw new SQLException("Unable to read DB config", e);
            }

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure driver is loaded
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }

            connection = DriverManager.getConnection(url, user, pass);
        }
        return connection;
    }
}
