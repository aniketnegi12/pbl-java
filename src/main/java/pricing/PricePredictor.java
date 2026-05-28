package main.java.pricing;

/**
 * AI-based Price Predictor
 * Uses profit calculation, competitor analysis, and demand patterns to predict optimal prices
 */
public class PricePredictor {

    /**
     * Predict optimal price for a product
     * @param costPrice Product cost price
     * @param competitorPrices Array of competitor prices
     * @param minMarginPercent Minimum margin percentage
     * @param demandLevel Demand level (0.0 to 1.0)
     * @return Predicted optimal selling price
     */
    public static double predictPrice(double costPrice, double[] competitorPrices, 
                                     double minMarginPercent, double demandLevel) {
        // Calculate minimum safe price based on margin requirement
        double minPrice = costPrice + (costPrice * minMarginPercent / 100);
        
        // Calculate average competitor price
        double avgCompetitorPrice = calculateAveragePrice(competitorPrices);
        
        // Competitive pricing strategy
        double competitivePrice = avgCompetitorPrice - 2.0; // Undercut competitors slightly
        
        // Demand-based adjustment
        double demandAdjustedPrice;
        if (demandLevel > 0.7) {
            // High demand: increase price
            demandAdjustedPrice = competitivePrice + 5.0;
        } else if (demandLevel < 0.3) {
            // Low demand: decrease price
            demandAdjustedPrice = competitivePrice - 5.0;
        } else {
            // Medium demand: match competitive price
            demandAdjustedPrice = competitivePrice;
        }
        
        // Ensure minimum profitability is maintained
        return Math.max(demandAdjustedPrice, minPrice);
    }

    /**
     * Advanced ML-inspired price prediction
     * Considers multiple factors: competitors, margin, demand, and market trends
     */
    public static double predictAdvancedPrice(double costPrice, double[] competitorPrices,
                                             double minMarginPercent, double demandLevel,
                                             double competitorAggression) {
        double minPrice = costPrice + (costPrice * minMarginPercent / 100);
        double avgCompetitor = calculateAveragePrice(competitorPrices);
        double maxCompetitor = findMax(competitorPrices);
        double minCompetitor = findMin(competitorPrices);
        
        // Market positioning score
        double marketRange = maxCompetitor - minCompetitor;
        double pricePosition = (avgCompetitor - minCompetitor) / marketRange; // 0 to 1
        
        // Adjust based on competitor pricing power
        double competitorAdjustedPrice = avgCompetitor - (competitorAggression * 3);
        
        // Demand elasticity
        double demandMultiplier = 0.8 + (demandLevel * 0.4); // 0.8x to 1.2x
        
        // Final price calculation
        double predictedPrice = competitorAdjustedPrice * demandMultiplier;
        
        // Apply market positioning
        if (pricePosition > 0.7) {
            predictedPrice *= 1.05; // Premium market positioning
        } else if (pricePosition < 0.3) {
            predictedPrice *= 0.95; // Budget market positioning
        }
        
        return Math.max(predictedPrice, minPrice);
    }

    /**
     * Calculate profit for a given selling price
     */
    public static double calculateProfit(double costPrice, double sellingPrice) {
        return sellingPrice - costPrice;
    }

    /**
     * Calculate profit margin percentage
     */
    public static double calculateMarginPercent(double costPrice, double sellingPrice) {
        double profit = calculateProfit(costPrice, sellingPrice);
        return (profit / costPrice) * 100;
    }

    /**
     * Check if price is profitable
     */
    public static boolean isProfitable(double costPrice, double sellingPrice) {
        return sellingPrice > costPrice;
    }

    /**
     * Check if price meets minimum margin requirement
     */
    public static boolean meetsMargin(double costPrice, double sellingPrice, double minMarginPercent) {
        double margin = calculateMarginPercent(costPrice, sellingPrice);
        return margin >= minMarginPercent;
    }

    /**
     * Calculate average competitor price
     */
    private static double calculateAveragePrice(double[] prices) {
        if (prices == null || prices.length == 0) {
            return 0;
        }
        double sum = 0;
        for (double price : prices) {
            sum += price;
        }
        return sum / prices.length;
    }

    /**
     * Find maximum price in array
     */
    private static double findMax(double[] prices) {
        double max = Double.NEGATIVE_INFINITY;
        for (double price : prices) {
            if (price > max) max = price;
        }
        return max;
    }

    /**
     * Find minimum price in array
     */
    private static double findMin(double[] prices) {
        double min = Double.POSITIVE_INFINITY;
        for (double price : prices) {
            if (price < min) min = price;
        }
        return min;
    }

    /**
     * Get price prediction explanation
     */
    public static String getPredictionExplanation(double costPrice, double sellingPrice,
                                                  double[] competitorPrices, double demandLevel) {
        double avgCompetitor = calculateAveragePrice(competitorPrices);
        double margin = calculateMarginPercent(costPrice, sellingPrice);
        
        String explanation = "Price Prediction Analysis:\n";
        explanation += "- Cost Price: $" + String.format("%.2f", costPrice) + "\n";
        explanation += "- Predicted Price: $" + String.format("%.2f", sellingPrice) + "\n";
        explanation += "- Profit Margin: " + String.format("%.1f", margin) + "%\n";
        explanation += "- Avg Competitor Price: $" + String.format("%.2f", avgCompetitor) + "\n";
        explanation += "- Demand Level: " + String.format("%.1f%%", demandLevel * 100) + "\n";
        
        if (demandLevel > 0.7) {
            explanation += "- Strategy: HIGH DEMAND - Pricing premium\n";
        } else if (demandLevel < 0.3) {
            explanation += "- Strategy: LOW DEMAND - Competitive pricing\n";
        } else {
            explanation += "- Strategy: MEDIUM DEMAND - Market pricing\n";
        }
        
        return explanation;
    }
}
