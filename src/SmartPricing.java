public class SmartPricingEngine {

    public static double suggestPrice(Product product, double minMargin) {

        double cost = product.getCostPrice();
        double avgCompetitor = ProfitCalculator.averageCompetitorPrice(product);

        // Minimum safe price
        double minPrice = cost + (cost * minMargin / 100);

        // 1. Competitive pricing
        double competitivePrice = avgCompetitor - 2;

        // 2. Demand simulation (simple logic)
        double demandFactor = Math.random(); // 0 to 1

        double demandAdjustedPrice;
        if (demandFactor > 0.7) {
            // High demand → increase price
            demandAdjustedPrice = competitivePrice + 5;
        } else if (demandFactor < 0.3) {
            // Low demand → decrease price
            demandAdjustedPrice = competitivePrice - 5;
        } else {
            demandAdjustedPrice = competitivePrice;
        }

        // 3. Final price should NEVER go below minimum margin
        return Math.max(demandAdjustedPrice, minPrice);
    }
}