package main.java;

import main.java.db.*;
import main.java.pricing.PricePredictor;
import java.util.List;
import java.util.Scanner;

/**
 * Integrated Pricing System Application
 * 
 * Workflow:
 * 1. SEARCH: Find products in database
 * 2. STORE: Add new products to database
 * 3. PREDICT: Use AI to calculate optimal prices
 * 4. SAVE: Store pricing predictions in database
 */
public class IntegratedPricingApp {
    
    private static ProductDao productDao;
    private static PricingHistoryDao pricingDao;
    private static Scanner scanner;

    public static void main(String[] args) {
        try {
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║  🤖 Integrated JDBC + AI Price Prediction System      ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝\n");

            // Initialize DAOs
            productDao = new ProductDao();
            pricingDao = new PricingHistoryDao();
            scanner = new Scanner(System.in);

            // Test connection
            System.out.println("1️⃣  Testing database connection...");
            if (!DatabaseUtil.testConnection()) {
                System.err.println("Cannot proceed without database connection.");
                return;
            }

            // Create tables
            System.out.println("\n2️⃣  Initializing database tables...");
            productDao.createTable();
            pricingDao.createTable();

            // Load sample data
            System.out.println("\n3️⃣  Loading sample data...");
            loadSampleData();

            // Main menu
            boolean running = true;
            while (running) {
                System.out.println("\n" + "=".repeat(55));
                System.out.println("           MAIN MENU");
                System.out.println("=".repeat(55));
                System.out.println("1. 🔍 SEARCH - Find products");
                System.out.println("2. ➕ ADD - Store new product");
                System.out.println("3. 🤖 PREDICT - AI price prediction");
                System.out.println("4. 📊 VIEW - Show all predictions");
                System.out.println("5. 📈 HISTORY - Pricing history");
                System.out.println("6. ❌ EXIT");
                System.out.println("=".repeat(55));
                System.out.print("Choose option (1-6): ");
                
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        searchProducts();
                        break;
                    case "2":
                        addProduct();
                        break;
                    case "3":
                        predictPrice();
                        break;
                    case "4":
                        viewAllPredictions();
                        break;
                    case "5":
                        viewPricingHistory();
                        break;
                    case "6":
                        running = false;
                        System.out.println("\n✅ Thank you for using the Integrated Pricing System!");
                        break;
                    default:
                        System.out.println("❌ Invalid option. Please try again.");
                }
            }

        } catch (Exception ex) {
            System.err.println("Application error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * STEP 1: SEARCH - Find products in database
     */
    private static void searchProducts() {
        try {
            System.out.println("\n" + "─".repeat(55));
            System.out.println("🔍 SEARCH PRODUCTS");
            System.out.println("─".repeat(55));
            System.out.print("Enter product name to search (or leave empty for all): ");
            String searchTerm = scanner.nextLine().trim();

            List<ProductDao.ProductData> results;
            if (searchTerm.isEmpty()) {
                results = productDao.getAllProducts();
                System.out.println("\n📋 All Products:");
            } else {
                results = productDao.searchProducts(searchTerm);
                System.out.println("\n📋 Search Results for '" + searchTerm + "':");
            }

            if (results.isEmpty()) {
                System.out.println("❌ No products found.");
            } else {
                System.out.println("─".repeat(55));
                for (int i = 0; i < results.size(); i++) {
                    ProductDao.ProductData product = results.get(i);
                    System.out.println((i + 1) + ". " + product);
                }
                System.out.println("─".repeat(55));
            }
        } catch (Exception ex) {
            System.err.println("Error searching products: " + ex.getMessage());
        }
    }

    /**
     * STEP 2: STORE - Add new product to database
     */
    private static void addProduct() {
        try {
            System.out.println("\n" + "─".repeat(55));
            System.out.println("➕ ADD NEW PRODUCT");
            System.out.println("─".repeat(55));
            
            System.out.print("Enter product name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter cost price ($): ");
            double costPrice = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter competitor prices (comma-separated, e.g., 100,105,110): ");
            String competitorPrices = scanner.nextLine().trim();

            productDao.insertProduct(name, costPrice, competitorPrices);
            System.out.println("✅ Product stored successfully!");

        } catch (NumberFormatException ex) {
            System.err.println("❌ Invalid number format. Please try again.");
        } catch (Exception ex) {
            System.err.println("Error adding product: " + ex.getMessage());
        }
    }

    /**
     * STEP 3: PREDICT - Use AI to calculate optimal price
     */
    private static void predictPrice() {
        try {
            System.out.println("\n" + "─".repeat(55));
            System.out.println("🤖 AI PRICE PREDICTION");
            System.out.println("─".repeat(55));

            // Show available products
            List<ProductDao.ProductData> products = productDao.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("❌ No products available. Please add products first.");
                return;
            }

            System.out.println("Available Products:");
            for (int i = 0; i < products.size(); i++) {
                System.out.println((i + 1) + ". " + products.get(i).name + " (Cost: $" + products.get(i).costPrice + ")");
            }

            System.out.print("\nSelect product number: ");
            int productIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (productIndex < 0 || productIndex >= products.size()) {
                System.out.println("❌ Invalid product selection.");
                return;
            }

            ProductDao.ProductData product = products.get(productIndex);

            // Get input parameters
            System.out.print("Enter minimum margin % (e.g., 20): ");
            double minMargin = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter demand level 0.0-1.0 (e.g., 0.75 for high demand): ");
            double demandLevel = Double.parseDouble(scanner.nextLine().trim());

            // Parse competitor prices
            double[] competitorPrices = parseCompetitorPrices(product.competitorPrices);

            // PREDICT PRICE USING AI
            double predictedPrice = PricePredictor.predictPrice(
                product.costPrice,
                competitorPrices,
                minMargin,
                demandLevel
            );

            double profit = PricePredictor.calculateProfit(product.costPrice, predictedPrice);
            double margin = PricePredictor.calculateMarginPercent(product.costPrice, predictedPrice);
            boolean isProfitable = PricePredictor.isProfitable(product.costPrice, predictedPrice);
            boolean meetMargin = PricePredictor.meetsMargin(product.costPrice, predictedPrice, minMargin);

            // Display prediction
            System.out.println("\n" + "═".repeat(55));
            System.out.println("🎯 PRICE PREDICTION RESULT");
            System.out.println("═".repeat(55));
            System.out.println("Product: " + product.name);
            System.out.println("Cost Price: $" + String.format("%.2f", product.costPrice));
            System.out.println("📊 PREDICTED SELLING PRICE: $" + String.format("%.2f", predictedPrice));
            System.out.println("─".repeat(55));
            System.out.println("Profit: $" + String.format("%.2f", profit));
            System.out.println("Margin: " + String.format("%.1f", margin) + "%");
            System.out.println("Profitable: " + (isProfitable ? "✅ YES" : "❌ NO"));
            System.out.println("Meets Minimum Margin: " + (meetMargin ? "✅ YES" : "❌ NO"));
            System.out.println("Demand Level: " + String.format("%.0f", demandLevel * 100) + "%");
            System.out.println("═".repeat(55));

            // Show explanation
            System.out.println("\n📝 ANALYSIS:");
            System.out.println(PricePredictor.getPredictionExplanation(
                product.costPrice, predictedPrice, competitorPrices, demandLevel
            ));

            // SAVE PREDICTION
            System.out.print("Save this prediction to database? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("y")) {
                String reason = "AI Predicted - Demand: " + String.format("%.0f", demandLevel * 100) + 
                               "%, Min Margin: " + String.format("%.0f", minMargin) + "%";
                pricingDao.insertPricePrediction(
                    product.id,
                    predictedPrice,
                    profit,
                    margin,
                    isProfitable,
                    meetMargin,
                    reason
                );
                System.out.println("✅ Prediction saved to database!");
            }

        } catch (NumberFormatException ex) {
            System.err.println("❌ Invalid number format. Please try again.");
        } catch (Exception ex) {
            System.err.println("Error predicting price: " + ex.getMessage());
        }
    }

    /**
     * View all predictions
     */
    private static void viewAllPredictions() {
        try {
            System.out.println("\n" + "─".repeat(55));
            System.out.println("📊 ALL PRICE PREDICTIONS");
            System.out.println("─".repeat(55));

            List<PricingHistoryDao.PricingData> predictions = pricingDao.getAllPricingHistory();

            if (predictions.isEmpty()) {
                System.out.println("❌ No predictions available.");
            } else {
                for (PricingHistoryDao.PricingData pred : predictions) {
                    System.out.println(pred);
                }
                System.out.println("─".repeat(55));
                System.out.println("Total predictions: " + predictions.size());
            }
        } catch (Exception ex) {
            System.err.println("Error viewing predictions: " + ex.getMessage());
        }
    }

    /**
     * View pricing history for a product
     */
    private static void viewPricingHistory() {
        try {
            System.out.println("\n" + "─".repeat(55));
            System.out.println("📈 PRICING HISTORY");
            System.out.println("─".repeat(55));

            List<ProductDao.ProductData> products = productDao.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("❌ No products available.");
                return;
            }

            System.out.println("Available Products:");
            for (int i = 0; i < products.size(); i++) {
                System.out.println((i + 1) + ". " + products.get(i).name);
            }

            System.out.print("\nSelect product number: ");
            int productIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (productIndex < 0 || productIndex >= products.size()) {
                System.out.println("❌ Invalid product selection.");
                return;
            }

            ProductDao.ProductData product = products.get(productIndex);
            List<PricingHistoryDao.PricingData> history = pricingDao.getPricingHistory(product.id);

            System.out.println("\nHistory for '" + product.name + "':");
            if (history.isEmpty()) {
                System.out.println("❌ No history available for this product.");
            } else {
                System.out.println("─".repeat(55));
                for (PricingHistoryDao.PricingData pred : history) {
                    System.out.println(pred);
                }
                System.out.println("─".repeat(55));
            }
        } catch (NumberFormatException ex) {
            System.err.println("❌ Invalid number format. Please try again.");
        } catch (Exception ex) {
            System.err.println("Error viewing history: " + ex.getMessage());
        }
    }

    /**
     * Load sample data
     */
    private static void loadSampleData() {
        try {
            // Check if data already exists
            List<ProductDao.ProductData> existing = productDao.getAllProducts();
            if (!existing.isEmpty()) {
                System.out.println("✓ Products already exist in database.");
                return;
            }

            // Add sample products
            productDao.insertProduct("Laptop", 500, "550,575,600");
            productDao.insertProduct("Smartphone", 200, "220,230,250");
            productDao.insertProduct("Headphones", 30, "35,40,45");
            productDao.insertProduct("Tablet", 150, "160,170,180");

            System.out.println("✓ Sample data loaded successfully.");
        } catch (Exception ex) {
            System.err.println("Error loading sample data: " + ex.getMessage());
        }
    }

    /**
     * Parse competitor prices string to array
     */
    private static double[] parseCompetitorPrices(String pricesStr) {
        String[] parts = pricesStr.split(",");
        double[] prices = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            prices[i] = Double.parseDouble(parts[i].trim());
        }
        return prices;
    }
}
