public class Product {

    private String name;
    private double costPrice;
    private double[] competitorPrices;

    public Product(String name, double costPrice, double[] competitorPrices) {
        if (costPrice <= 0) {
            throw new IllegalArgumentException("Cost price must be > 0");
        }
        this.name = name;
        this.costPrice = costPrice;
        this.competitorPrices = competitorPrices;
    }

    public String getName() {
        return name;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public double[] getCompetitorPrices() {
        return competitorPrices;
    }
}