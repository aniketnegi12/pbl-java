// ================================================================
// Member 1 - AI Pricing Engine + System Architecture
// AI-Enabled Online Product Price and Profit Extension System
// ================================================================

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

// ── Product: stores all inputs ───────────────────────────────────
class Product {
    String   name;
    double   costPrice;
    double[] competitorPrices;
    String   demandLevel;   // HIGH / MEDIUM / LOW  or  1-10
    double   profitMargin;  // e.g. 20 = 20%

    Product(String name, double costPrice, double[] competitorPrices,
            String demandLevel, double profitMargin) {
        this.name             = name;
        this.costPrice        = costPrice;
        this.competitorPrices = competitorPrices;
        this.demandLevel      = demandLevel;
        this.profitMargin     = profitMargin;
    }
}

// ── AIPricingEngine: OpenAI first, rule-based fallback ───────────
class AIPricingEngine {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final String apiKey;
    public  String lastInsight   = "";
    public  boolean usedOpenAI   = false;

    AIPricingEngine(String apiKey) { this.apiKey = apiKey; }

    // Main method: returns suggested price
    double suggestPrice(double competitorAvg, String demand,
                        double minPrice, String season) {

        // Try OpenAI first
        if (apiKey != null) {
            double aiPrice = callOpenAI(competitorAvg, demand, minPrice, season);
            if (aiPrice > 0) {
                usedOpenAI  = true;
                lastInsight = "[OpenAI] Smart pricing applied based on market analysis.";
                return Math.max(aiPrice, minPrice); // profit protection
            }
        }

        // Fallback: rule-based logic
        return ruleBased(competitorAvg, demand, minPrice, season);
    }

    // Rule-based engine
    private double ruleBased(double avg, String demand, double minPrice, String season) {
        usedOpenAI = false;

        // Demand rule
        double price;
        switch (demand.toUpperCase()) {
            case "VERY_HIGH": price = avg * 1.10; lastInsight = "Very high demand — premium pricing (+10%)."; break;
            case "HIGH":      price = avg * 1.05; lastInsight = "High demand — premium pricing (+5%).";       break;
            case "LOW":       price = avg * 0.95; lastInsight = "Low demand — discount pricing (-5%).";       break;
            case "VERY_LOW":  price = avg * 0.90; lastInsight = "Very low demand — clearance pricing (-10%)."; break;
            default:          price = avg * 1.00; lastInsight = "Moderate demand — competitive pricing.";     break;
        }

        // Season rule
        if (season.equalsIgnoreCase("PEAK")) price *= 1.08;
        if (season.equalsIgnoreCase("OFF"))  price *= 0.95;

        // Profit protection: never sell below minimum
        if (price < minPrice) {
            price       = minPrice;
            lastInsight += " (Adjusted to protect profit margin.)";
        }

        return Math.round(price * 100.0) / 100.0;
    }

    // OpenAI API call
    private double callOpenAI(double competitorAvg, String demand,
                               double minPrice, String season) {
        try {
            String prompt = "You are a pricing analyst. Suggest a selling price.\n"
                    + "Competitor Avg: " + competitorAvg + ", Demand: " + demand
                    + ", Season: " + season + ", Min Price: " + minPrice + "\n"
                    + "Rules: Price must not go below " + minPrice + ".\n"
                    + "Reply ONLY: PRICE: <number>";

            String body = "{\"model\":\"gpt-3.5-turbo\",\"messages\":["
                    + "{\"role\":\"user\",\"content\":\""
                    + prompt.replace("\n", "\\n") + "\"}],"
                    + "\"max_tokens\":20,\"temperature\":0.3}";

            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(10000);
            conn.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));

            if (conn.getResponseCode() != 200) return -1;

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            // Extract price from response
            String response = sb.toString();
            int idx = response.lastIndexOf("\"content\":\"");
            if (idx == -1) return -1;
            String content = response.substring(idx + 11, response.indexOf("\"", idx + 11));
            content = content.replace("\\n", "\n");
            for (String l : content.split("\n")) {
                if (l.contains("PRICE:")) {
                    return Double.parseDouble(l.replace("PRICE:", "").replaceAll("[^0-9.]", "").trim());
                }
            }
        } catch (Exception e) {
            System.out.println("[OpenAI] Failed: " + e.getMessage());
        }
        return -1;
    }

    // Convert numeric demand score (1-10) to label
    static String fromScore(int score) {
        if (score >= 9) return "VERY_HIGH";
        if (score >= 7) return "HIGH";
        if (score >= 4) return "MEDIUM";
        if (score >= 2) return "LOW";
        return "VERY_LOW";
    }
}

// ── PricingAlgorithm: System Architecture / Integration Layer ────
class PricingAlgorithm {

    private final AIPricingEngine engine;

    PricingAlgorithm() {
        // Load API key from config.properties (never hardcode)
        String key = loadApiKey();
        this.engine = new AIPricingEngine(key);
    }

    // Full pipeline: Product → Calculate → AI → Output
    void run(Product p, String season) {

        // Validate
        if (p.costPrice <= 0 || p.competitorPrices.length == 0) {
            System.out.println("❌ Error: Invalid input for " + p.name); return;
        }

        // Step 1: Competitor average  →  O(n)
        double avg = 0;
        for (double cp : p.competitorPrices) avg += cp;
        avg = Math.round((avg / p.competitorPrices.length) * 100.0) / 100.0;

        // Step 2: Minimum profit price  →  O(1)
        double minPrice = p.costPrice + (p.costPrice * p.profitMargin / 100);

        // Step 3: Resolve numeric demand if needed
        String demand = p.demandLevel;
        try { demand = AIPricingEngine.fromScore(Integer.parseInt(demand)); }
        catch (NumberFormatException ignored) {}

        // Step 4: AI Pricing Engine (OpenAI or rule-based)  →  O(1)
        double suggested = engine.suggestPrice(avg, demand, minPrice, season);

        // Step 5: Profit metrics  →  O(1)
        double profit    = Math.round((suggested - p.costPrice) * 100.0) / 100.0;
        double profitPct = Math.round((profit / p.costPrice) * 10000.0) / 100.0;

        // Step 6: Evaluation metrics
        double prefSatisfaction = Math.min((suggested / minPrice) * 100, 100.0);
        double utilEfficiency   = (avg - p.costPrice) > 0
                                  ? (profit / (avg - p.costPrice)) * 100 : 100.0;

        // Output
        printResult(p.name, p.costPrice, avg, minPrice, demand, season,
                    suggested, profit, profitPct, prefSatisfaction,
                    utilEfficiency, engine.lastInsight, engine.usedOpenAI);
    }

    void run(Product p) { run(p, "NORMAL"); }

    private void printResult(String name, double cost, double avg, double minPrice,
                              String demand, String season, double suggested,
                              double profit, double profitPct, double prefSat,
                              double utilEff, String insight, boolean usedAI) {
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.printf ("│ Product       : %-24s│%n", name);
        System.out.printf ("│ Cost Price    : ₹%-23.2f│%n", cost);
        System.out.printf ("│ Competitor Avg: ₹%-23.2f│%n", avg);
        System.out.printf ("│ Demand        : %-24s│%n", demand);
        System.out.printf ("│ Season        : %-24s│%n", season);
        System.out.printf ("│ Engine Used   : %-24s│%n", usedAI ? "OpenAI GPT" : "Rule-Based");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.printf ("│ ✅ Suggested Price : ₹%-19.2f│%n", suggested);
        System.out.printf ("│ 💰 Profit          : ₹%-19.2f│%n", profit);
        System.out.printf ("│ 📈 Profit %%        : %-19.2f│%n", profitPct);
        System.out.printf ("│ 📊 Pref. Satisf.   : %-19.2f│%n", Math.round(prefSat * 100) / 100.0);
        System.out.printf ("│ ⚙  Util. Efficiency: %-19.2f│%n", Math.round(utilEff * 100) / 100.0);
        System.out.println("├─────────────────────────────────────────┤");
        System.out.printf ("│ 💡 %-38s│%n", insight.length() > 38 ? insight.substring(0, 38) : insight);
        System.out.println("└─────────────────────────────────────────┘\n");
    }

    private String loadApiKey() {
        try (InputStream in = new FileInputStream("config.properties")) {
            java.util.Properties p = new java.util.Properties();
            p.load(in);
            String key = p.getProperty("OPENAI_API_KEY");
            if (key != null && !key.trim().isEmpty()) return key.trim();
        } catch (Exception ignored) {}
        String env = System.getenv("OPENAI_API_KEY");
        return (env != null && !env.trim().isEmpty()) ? env.trim() : null;
    }
}

// ── Main: run all test cases ─────────────────────────────────────
public class Member1Module {

    public static void main(String[] args) {

        PricingAlgorithm algo = new PricingAlgorithm();

        System.out.println("=== Member 1 — AI Pricing Engine Tests ===\n");

        algo.run(new Product("Winter Jacket",  1500, new double[]{2200,2400,2300}, "VERY_HIGH", 25), "PEAK");
        algo.run(new Product("Nike Shoes",      800, new double[]{1100,1050,1200}, "HIGH",      20), "NORMAL");
        algo.run(new Product("Coffee Mug",       80, new double[]{120,130,110},    "MEDIUM",    15), "NORMAL");
        algo.run(new Product("Sunscreen",       150, new double[]{200,210,190},    "LOW",       10), "OFF");
        algo.run(new Product("Xmas Deco",        50, new double[]{80,90,70},       "VERY_LOW",   5), "OFF");

        // Profit protection test
        algo.run(new Product("Laptop",        45000, new double[]{46000,45500},    "LOW",       15), "NORMAL");

        // Numeric demand score (from UI slider)
        algo.run(new Product("Headphones",     1200, new double[]{1800,1750,1900}, "8",         20), "NORMAL");

        // Error cases
        algo.run(new Product("Bad Item",          0, new double[]{100,120},        "HIGH",      20), "NORMAL");
        algo.run(new Product("No Competitors",  500, new double[]{},               "HIGH",      20), "NORMAL");
    }
}