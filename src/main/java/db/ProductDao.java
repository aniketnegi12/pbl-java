package main.java.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product entity.
 * Handles CRUD operations and searches for products.
 */
public class ProductDao {

    /**
     * Create the products table
     */
    public void createTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL UNIQUE, " +
                "cost_price DECIMAL(10, 2) NOT NULL, " +
                "competitor_prices VARCHAR(255), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ Products table created or already exists.");
        } catch (SQLException ex) {
            System.err.println("Error creating products table: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Insert a new product
     * @param name Product name
     * @param costPrice Cost price
     * @param competitorPrices Comma-separated competitor prices (e.g., "100,105,110")
     */
    public int insertProduct(String name, double costPrice, String competitorPrices) throws Exception {
        String sql = "INSERT INTO products (name, cost_price, competitor_prices) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, costPrice);
            pstmt.setString(3, competitorPrices);
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int productId = rs.getInt(1);
                    System.out.println("✓ Product '" + name + "' inserted with ID: " + productId);
                    return productId;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error inserting product: " + ex.getMessage());
            throw ex;
        }
        return -1;
    }

    /**
     * Search products by name
     * @param searchTerm Product name or partial name
     */
    public List<ProductData> searchProducts(String searchTerm) throws Exception {
        String sql = "SELECT * FROM products WHERE name LIKE ?";
        List<ProductData> products = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductData product = new ProductData(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("cost_price"),
                        rs.getString("competitor_prices"),
                        rs.getTimestamp("created_at").toString()
                    );
                    products.add(product);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error searching products: " + ex.getMessage());
            throw ex;
        }
        return products;
    }

    /**
     * Get all products
     */
    public List<ProductData> getAllProducts() throws Exception {
        String sql = "SELECT * FROM products";
        List<ProductData> products = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ProductData product = new ProductData(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("cost_price"),
                    rs.getString("competitor_prices"),
                    rs.getTimestamp("created_at").toString()
                );
                products.add(product);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving products: " + ex.getMessage());
            throw ex;
        }
        return products;
    }

    /**
     * Get product by ID
     */
    public ProductData getProductById(int id) throws Exception {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductData(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("cost_price"),
                        rs.getString("competitor_prices"),
                        rs.getTimestamp("created_at").toString()
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving product: " + ex.getMessage());
            throw ex;
        }
        return null;
    }

    /**
     * Update product
     */
    public void updateProduct(int id, String name, double costPrice, String competitorPrices) throws Exception {
        String sql = "UPDATE products SET name = ?, cost_price = ?, competitor_prices = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, costPrice);
            pstmt.setString(3, competitorPrices);
            pstmt.setInt(4, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✓ Product updated. Rows affected: " + rowsAffected);
        } catch (SQLException ex) {
            System.err.println("Error updating product: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Delete product
     */
    public void deleteProduct(int id) throws Exception {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✓ Product deleted. Rows affected: " + rowsAffected);
        } catch (SQLException ex) {
            System.err.println("Error deleting product: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Product data model
     */
    public static class ProductData {
        public int id;
        public String name;
        public double costPrice;
        public String competitorPrices;
        public String createdAt;

        public ProductData(int id, String name, double costPrice, String competitorPrices, String createdAt) {
            this.id = id;
            this.name = name;
            this.costPrice = costPrice;
            this.competitorPrices = competitorPrices;
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "ID: " + id + " | Name: " + name + " | Cost: $" + costPrice + " | Competitors: " + competitorPrices;
        }
    }
}
