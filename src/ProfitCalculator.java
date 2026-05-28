/**
 * Profit calculation utility for pricing analysis
 * Requires: Product class (must be in same source directory)
 */
public class ProfitCalculator {

    public static double calculateProfit(Product product, double sellingPrice) {
        return sellingPrice - product.getCostPrice();
    }

    public static double calculateMargin(Product product, double sellingPrice) {
        double profit = calculateProfit(product, sellingPrice);
        // Line 12: Calculate margin percentage
        return (profit / product.getCostPrice()) * 100;
    }

    public static double averageCompetitorPrice(Product product) {
        // Line 16: Get competitor prices from product
        double[] prices = product.getCompetitorPrices();

        if (prices == null || prices.length == 0) {
            throw new IllegalArgumentException("No competitor data");
        }

        double sum = 0;
        for (double p : prices) {
            sum += p;
        }

        return sum / prices.length;
    }
}