package main.java;

import main.java.pricing.PricePredictor;
import java.util.Scanner;

/**
 * Integrated JDBC + AI Price Prediction Demo
 * 
 * Demonstrates the complete workflow:
 * 1. SEARCH Products (simulated database)
 * 2. STORE Product Data (database ready)
 * 3. PREDICT Optimal Prices using AI
 */
public class IntegratedDemo {
    
    static class Product {
        int id;
        String name;
        double costPrice;
        double[] competitorPrices;
        
        Product(int id, String name, double costPrice, double[] competitorPrices) {
            this.id = id;
            this.name = name;
            this.costPrice = costPrice;
            this.competitorPrices = competitorPrices;
        }
    }
    
    static class PricingPrediction {
        int productId;
        double predictedPrice;
        double profit;
        double margin;
        boolean isProfitable;
        boolean meetMargin;
        
        PricingPrediction(int productId, double predictedPrice, double profit, 
                         double margin, boolean isProfitable, boolean meetMargin) {
            this.productId = productId;
            this.predictedPrice = predictedPrice;
            this.profit = profit;
            this.margin = margin;
            this.isProfitable = isProfitable;
            this.meetMargin = meetMargin;
        }
    }
    
    static Product[] sampleProducts = {
        new Product(1, "Laptop", 500, new double[]{550, 575, 600}),
        new Product(2, "Smartphone", 200, new double[]{220, 230, 250}),
        new Product(3, "Headphones", 30, new double[]{35, 40, 45}),
        new Product(4, "Tablet", 150, new double[]{160, 170, 180}),
        new Product(5, "Monitor", 200, new double[]{220, 240, 260})
    };
    
    static java.util.List<PricingPrediction> predictions = new java.util.ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║  🤖 JDBC + AI Integrated Price Prediction System       ║");
            System.out.println("║  WORKFLOW: SEARCH → STORE → PREDICT                   ║");
            System.out.println("╚════════════════════════════════════════════════════════╝\n");

            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        searchProducts();
                        break;
                    case "2":
                        storeProduct();
                        break;
                    case "3":
                        predictPrice();
                        break;
                    case "4":
                        viewPredictions();
                        break;
                    case "5":
                        System.out.println("\n✅ Thank you for using the Integrated System!");
                        running = false;
                        break;
                    default:
                        System.out.println("❌ Invalid option.");
                }
            }

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("\n" + "=".repeat(55));
        System.out.println("1. 🔍 SEARCH - Find products from database");
        System.out.println("2. ➕ STORE - Add product to database");
        System.out.println("3. 🤖 PREDICT - AI price prediction");
        System.out.println("4. 📊 VIEW - Show all predictions");
        System.out.println("5. ❌ EXIT");
        System.out.println("=".repeat(55));
        System.out.print("Choose (1-5): ");
    }

    private static void searchProducts() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("🔍 SEARCH PRODUCTS (From Database)");
        System.out.println("─".repeat(55));
        System.out.print("Enter product name to search: ");
        String search = scanner.nextLine().toLowerCase();

        boolean found = false;
        for (Product p : sampleProducts) {
            if (p.name.toLowerCase().contains(search)) {
                System.out.println("✓ Found: " + p.name + " (ID: " + p.id + ", Cost: $" + p.costPrice + ")");
                found = true;
            }
        }
        if (!found) {
            System.out.println("❌ No products found");
        }
    }

    private static void storeProduct() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("➕ STORE PRODUCT (To Database)");
        System.out.println("─".repeat(55));
        System.out.print("Product name: ");
        String name = scanner.nextLine();
        System.out.print("Cost price: ");
        double cost = Double.parseDouble(scanner.nextLine());
        System.out.print("Competitor prices (comma-sep): ");
        String[] priceStrs = scanner.nextLine().split(",");
        double[] competitorPrices = new double[priceStrs.length];
        for (int i = 0; i < priceStrs.length; i++) {
            competitorPrices[i] = Double.parseDouble(priceStrs[i].trim());
        }
        
        System.out.println("✅ Product stored to database: " + name);
        System.out.println("   SQL: INSERT INTO products (name, cost_price, competitor_prices) VALUES ('" +
                         name + "', " + cost + ", '" + java.util.Arrays.toString(competitorPrices) + "')");
    }

    private static void predictPrice() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("🤖 AI PRICE PREDICTION");
        System.out.println("─".repeat(55));
        
        System.out.println("\nAvailable Products:");
        for (int i = 0; i < sampleProducts.length; i++) {
            System.out.println((i+1) + ". " + sampleProducts[i].name + " (Cost: $" + sampleProducts[i].costPrice + ")");
        }

        System.out.print("\nSelect product (1-" + sampleProducts.length + "): ");
        int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        Product p = sampleProducts[idx];

        System.out.print("Minimum margin % (e.g., 20): ");
        double minMargin = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Demand level 0.0-1.0 (e.g., 0.75 for high): ");
        double demand = Double.parseDouble(scanner.nextLine().trim());

        // PREDICT PRICE
        double predictedPrice = PricePredictor.predictPrice(p.costPrice, p.competitorPrices, minMargin, demand);
        double profit = PricePredictor.calculateProfit(p.costPrice, predictedPrice);
        double margin = PricePredictor.calculateMarginPercent(p.costPrice, predictedPrice);
        boolean isProfitable = PricePredictor.isProfitable(p.costPrice, predictedPrice);
        boolean meetMargin = PricePredictor.meetsMargin(p.costPrice, predictedPrice, minMargin);

        System.out.println("\n" + "═".repeat(55));
        System.out.println("🎯 PRICE PREDICTION RESULT");
        System.out.println("═".repeat(55));
        System.out.println("Product: " + p.name);
        System.out.println("Cost Price: $" + String.format("%.2f", p.costPrice));
        System.out.println("📊 PREDICTED SELLING PRICE: $" + String.format("%.2f", predictedPrice));
        System.out.println("Profit: $" + String.format("%.2f", profit));
        System.out.println("Margin: " + String.format("%.1f", margin) + "%");
        System.out.println("Profitable: " + (isProfitable ? "✅ YES" : "❌ NO"));
        System.out.println("Meets Min Margin: " + (meetMargin ? "✅ YES" : "❌ NO"));
        System.out.println("Demand: " + String.format("%.0f", demand * 100) + "%");
        System.out.println("═".repeat(55));

        System.out.println("\n📝 ANALYSIS:");
        System.out.println(PricePredictor.getPredictionExplanation(p.costPrice, predictedPrice, p.competitorPrices, demand));

        System.out.print("Save prediction to database? (y/n): ");
        if (scanner.nextLine().toLowerCase().equals("y")) {
            PricingPrediction pred = new PricingPrediction(p.id, predictedPrice, profit, margin, isProfitable, meetMargin);
            predictions.add(pred);
            System.out.println("✅ Prediction saved to database");
            System.out.println("   SQL: INSERT INTO pricing_history (product_id, predicted_price, profit, margin_percent, is_profitable, meets_margin)");
            System.out.println("   VALUES (" + p.id + ", " + String.format("%.2f", predictedPrice) + ", " + 
                             String.format("%.2f", profit) + ", " + String.format("%.1f", margin) + ", " + 
                             isProfitable + ", " + meetMargin + ")");
        }
    }

    private static void viewPredictions() {
        System.out.println("\n" + "─".repeat(55));
        System.out.println("📊 ALL PRICE PREDICTIONS (From Database)");
        System.out.println("─".repeat(55));
        
        if (predictions.isEmpty()) {
            System.out.println("❌ No predictions stored");
        } else {
            for (int i = 0; i < predictions.size(); i++) {
                PricingPrediction p = predictions.get(i);
                System.out.println((i+1) + ". Product ID: " + p.productId + 
                                 " | Price: $" + String.format("%.2f", p.predictedPrice) + 
                                 " | Profit: $" + String.format("%.2f", p.profit) + 
                                 " | Margin: " + String.format("%.1f", p.margin) + "%");
            }
        }
    }
}
