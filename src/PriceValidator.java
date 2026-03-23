public class PriceValidator {

    public static boolean isProfitable(Product product, double sellingPrice) {
        return sellingPrice > product.getCostPrice();
    }

    public static boolean meetsMargin(Product product, double sellingPrice, double minMargin) {
        double margin = (sellingPrice - product.getCostPrice()) / product.getCostPrice() * 100;
        return margin >= minMargin;
    }
}