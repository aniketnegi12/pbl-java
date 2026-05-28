package main.java.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Pricing History.
 * Stores predicted prices and pricing decisions.
 */
public class PricingHistoryDao {

    /**
     * Create the pricing history table
     */
    public void createTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS pricing_history (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "product_id INT NOT NULL, " +
                "predicted_price DECIMAL(10, 2) NOT NULL, " +
                "profit DECIMAL(10, 2) NOT NULL, " +
                "margin_percent DECIMAL(5, 2) NOT NULL, " +
                "is_profitable BOOLEAN NOT NULL, " +
                "meets_margin BOOLEAN NOT NULL, " +
                "prediction_reason VARCHAR(255), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE)";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ Pricing history table created or already exists.");
        } catch (SQLException ex) {
            System.err.println("Error creating pricing history table: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Insert a pricing prediction
     */
    public void insertPricePrediction(int productId, double predictedPrice, double profit, 
                                     double marginPercent, boolean isProfitable, boolean meetMargin,
                                     String reason) throws Exception {
        String sql = "INSERT INTO pricing_history (product_id, predicted_price, profit, margin_percent, " +
                "is_profitable, meets_margin, prediction_reason) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setDouble(2, predictedPrice);
            pstmt.setDouble(3, profit);
            pstmt.setDouble(4, marginPercent);
            pstmt.setBoolean(5, isProfitable);
            pstmt.setBoolean(6, meetMargin);
            pstmt.setString(7, reason);
            pstmt.executeUpdate();
            System.out.println("✓ Price prediction stored (ID: " + productId + ", Price: $" + predictedPrice + ")");
        } catch (SQLException ex) {
            System.err.println("Error inserting price prediction: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Get pricing history for a product
     */
    public List<PricingData> getPricingHistory(int productId) throws Exception {
        String sql = "SELECT * FROM pricing_history WHERE product_id = ? ORDER BY created_at DESC";
        List<PricingData> history = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PricingData data = new PricingData(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getDouble("predicted_price"),
                        rs.getDouble("profit"),
                        rs.getDouble("margin_percent"),
                        rs.getBoolean("is_profitable"),
                        rs.getBoolean("meets_margin"),
                        rs.getString("prediction_reason"),
                        rs.getTimestamp("created_at").toString()
                    );
                    history.add(data);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving pricing history: " + ex.getMessage());
            throw ex;
        }
        return history;
    }

    /**
     * Get all pricing predictions
     */
    public List<PricingData> getAllPricingHistory() throws Exception {
        String sql = "SELECT * FROM pricing_history ORDER BY created_at DESC";
        List<PricingData> history = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PricingData data = new PricingData(
                    rs.getInt("id"),
                    rs.getInt("product_id"),
                    rs.getDouble("predicted_price"),
                    rs.getDouble("profit"),
                    rs.getDouble("margin_percent"),
                    rs.getBoolean("is_profitable"),
                    rs.getBoolean("meets_margin"),
                    rs.getString("prediction_reason"),
                    rs.getTimestamp("created_at").toString()
                );
                history.add(data);
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving all pricing history: " + ex.getMessage());
            throw ex;
        }
        return history;
    }

    /**
     * Get latest prediction for a product
     */
    public PricingData getLatestPrediction(int productId) throws Exception {
        String sql = "SELECT * FROM pricing_history WHERE product_id = ? ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new PricingData(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getDouble("predicted_price"),
                        rs.getDouble("profit"),
                        rs.getDouble("margin_percent"),
                        rs.getBoolean("is_profitable"),
                        rs.getBoolean("meets_margin"),
                        rs.getString("prediction_reason"),
                        rs.getTimestamp("created_at").toString()
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving latest prediction: " + ex.getMessage());
            throw ex;
        }
        return null;
    }

    /**
     * Pricing data model
     */
    public static class PricingData {
        public int id;
        public int productId;
        public double predictedPrice;
        public double profit;
        public double marginPercent;
        public boolean isProfitable;
        public boolean meetMargin;
        public String reason;
        public String createdAt;

        public PricingData(int id, int productId, double predictedPrice, double profit, 
                          double marginPercent, boolean isProfitable, boolean meetMargin,
                          String reason, String createdAt) {
            this.id = id;
            this.productId = productId;
            this.predictedPrice = predictedPrice;
            this.profit = profit;
            this.marginPercent = marginPercent;
            this.isProfitable = isProfitable;
            this.meetMargin = meetMargin;
            this.reason = reason;
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "Prediction #" + id + " | Price: $" + String.format("%.2f", predictedPrice) + 
                   " | Profit: $" + String.format("%.2f", profit) + 
                   " | Margin: " + String.format("%.1f", marginPercent) + "% | " +
                   "Profitable: " + isProfitable + " | Meets Margin: " + meetMargin;
        }
    }
}
