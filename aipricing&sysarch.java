/**
 * Product.java
 * Member 1 – Product Data Model (used by all modules)
 * Project: AI-Enabled Online Product Price and Profit Extension System
 */

public class Product {

    private int      productId;
    private String   name;
    private double   costPrice;
    private double[] competitorPrices;
    private String   demandLevel;        // "VERY_HIGH","HIGH","MEDIUM","LOW","VERY_LOW" or "1"–"10"
    private double   desiredProfitMargin; // in percentage, e.g. 20 = 20%
    private String   category;           // optional: e.g. "Electronics", "Clothing"

    // ─────────────────────────────────────────────
    // CONSTRUCTORS
    // ─────────────────────────────────────────────

    public Product() {}

    public Product(String name, double costPrice, double[] competitorPrices,
                   String demandLevel, double desiredProfitMargin) {
        this.name                 = name;
        this.costPrice            = costPrice;
        this.competitorPrices     = competitorPrices;
        this.demandLevel          = demandLevel;
        this.desiredProfitMargin  = desiredProfitMargin;
    }

    public Product(int productId, String name, double costPrice, double[] competitorPrices,
                   String demandLevel, double desiredProfitMargin, String category) {
        this.productId            = productId;
        this.name                 = name;
        this.costPrice            = costPrice;
        this.competitorPrices     = competitorPrices;
        this.demandLevel          = demandLevel;
        this.desiredProfitMargin  = desiredProfitMargin;
        this.category             = category;
    }

    // ─────────────────────────────────────────────
    // GETTERS & SETTERS
    // ─────────────────────────────────────────────

    public int      getProductId()            { return productId; }
    public String   getName()                 { return name; }
    public double   getCostPrice()            { return costPrice; }
    public double[] getCompetitorPrices()     { return competitorPrices; }
    public String   getDemandLevel()          { return demandLevel; }
    public double   getDesiredProfitMargin()  { return desiredProfitMargin; }
    public String   getCategory()             { return category; }

    public void setProductId(int productId)                     { this.productId = productId; }
    public void setName(String name)                             { this.name = name; }
    public void setCostPrice(double costPrice)                   { this.costPrice = costPrice; }
    public void setCompetitorPrices(double[] competitorPrices)   { this.competitorPrices = competitorPrices; }
    public void setDemandLevel(String demandLevel)               { this.demandLevel = demandLevel; }
    public void setDesiredProfitMargin(double desiredProfitMargin){ this.desiredProfitMargin = desiredProfitMargin; }
    public void setCategory(String category)                     { this.category = category; }

    // ─────────────────────────────────────────────
    // toString (for debugging / storage)
    // ─────────────────────────────────────────────

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Product{")
          .append("id=").append(productId)
          .append(", name='").append(name).append('\'')
          .append(", costPrice=").append(costPrice)
          .append(", demandLevel='").append(demandLevel).append('\'')
          .append(", profitMargin=").append(desiredProfitMargin).append("%")
          .append(", competitors=[");
        if (competitorPrices != null) {
            for (int i = 0; i < competitorPrices.length; i++) {
                sb.append(competitorPrices[i]);
                if (i < competitorPrices.length - 1) sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}