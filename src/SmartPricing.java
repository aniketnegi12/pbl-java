public class SmartPricingEngine {

    public static double suggestPrice(Product product, double minMargin) {

        double cost= product.getCostPrice();
        double avgCompetitor= ProfitCalculator.averageCompetitorPrice(product);

        // Minimum safe price
        double minPrice= cost+(cost*minMargin/100);

        double competitivePrice= avgCompetitor- 2;
        double demandFactor= Math.random();

        double demandAdjustedPrice;
        if (demandFactor>0.7) {
            demandAdjustedPrice= competitivePrice+ 5;
        } else if (demandFactor<0.3) {
            demandAdjustedPrice= competitivePrice- 5;
        } else {
            demandAdjustedPrice= competitivePrice;
        }
        return Math.max(demandAdjustedPrice,minPrice);
    }
}
