package main.java.db;

import java.sql.*;

/**
 * Data Access Object (DAO) for User entity.
 * Provides CRUD (Create, Read, Update, Delete) operations for users table.
 */
public class UserDao {

    /**
     * Create the users table if it doesn't exist
     */
    public void createTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ Users table created or already exists.");
        } catch (SQLException ex) {
            System.err.println("Error creating table: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Insert a new user
     * @param name User's name
     * @param email User's email
     */
    public void insertUser(String name, String email) throws Exception {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✓ User inserted successfully. Rows affected: " + rowsAffected);
        } catch (SQLException ex) {
            System.err.println("Error inserting user: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Get all users
     */
    public void getUsers() throws Exception {
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Users in Database ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Timestamp createdAt = rs.getTimestamp("created_at");
                System.out.println("ID: " + id + " | Name: " + name + " | Email: " + email + " | Created: " + createdAt);
            }
            System.out.println("------------------------\n");
        } catch (SQLException ex) {
            System.err.println("Error retrieving users: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Get a user by ID
     * @param id User's ID
     */
    public void getUserById(int id) throws Exception {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    System.out.println("User Found - ID: " + id + " | Name: " + name + " | Email: " + email);
                } else {
                    System.out.println("User with ID " + id + " not found.");
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving user: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Update a user
     * @param id User's ID
     * @param name New name
     * @param email New email
     */
    public void updateUser(int id, String name, String email) throws Exception {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✓ User updated successfully. Rows affected: " + rowsAffected);
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Delete a user
     * @param id User's ID
     */
    public void deleteUser(int id) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✓ User deleted successfully. Rows affected: " + rowsAffected);
        } catch (SQLException ex) {
            System.err.println("Error deleting user: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Delete all users
     */
    public void deleteAllUsers() throws Exception {
        String sql = "DELETE FROM users";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("✓ All users deleted successfully. Rows affected: " + rowsAffected);
        } catch (SQLException ex) {
            System.err.println("Error deleting users: " + ex.getMessage());
            throw ex;
        }
    }
}
