public class MainTest {

    public static void main(String[] args) {

        double[] competitors = {120, 130, 125};

        Product product = new Product("Laptop", 100, competitors);

        double suggestedPrice = SmartPricingEngine.suggestPrice(product, 20);

        double profit = ProfitCalculator.calculateProfit(product, suggestedPrice);
        double margin = ProfitCalculator.calculateMargin(product, suggestedPrice);

        System.out.println("Product: " + product.getName());
        System.out.println("Suggested Price: " + suggestedPrice);
        System.out.println("Profit: " + profit);
        System.out.println("Margin: " + margin + "%");

        System.out.println("Valid Price? " +
                PriceValidator.isProfitable(product, suggestedPrice));
    }
}