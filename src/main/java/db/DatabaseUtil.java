package main.java.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;

/**
 * Utility class to manage database connections using JDBC.
 * Loads configuration from db.properties file.
 */
public class DatabaseUtil {
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
                url = prop.getProperty("db.url");
                user = prop.getProperty("db.user");
                password = prop.getProperty("db.password");
                String driver = prop.getProperty("db.driver");
                Class.forName(driver);
                System.out.println("Database driver loaded successfully.");
            } else {
                System.err.println("db.properties file not found!");
            }
        } catch (Exception ex) {
            System.err.println("Error loading database configuration: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Get a database connection
     * @return Connection object
     * @throws Exception if connection fails
     */
    public static Connection getConnection() throws Exception {
        if (url == null || user == null || password == null) {
            throw new Exception("Database configuration not loaded. Check db.properties file.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Test the database connection
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            conn.isValid(2);
            System.out.println("✓ Database connection successful!");
            return true;
        } catch (Exception ex) {
            System.err.println("✗ Database connection failed: " + ex.getMessage());
            return false;
        }
    }
}
