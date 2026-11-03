package ShopEase;

public class Product {
    private int productId;
    private String name;
    private String category;
    private double price;
    private int stockQty;
    private String imageUrl;

    public Product(int productId, String name, String category, double price, int stockQty, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQty = stockQty;
        this.imageUrl = imageUrl;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStockQty() { return stockQty; }
    public void setStockQty(int stockQty) { this.stockQty = stockQty; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return "Product{" + "id=" + productId + ", name='" + name + '\'' + ", price=" + price + ", stock=" + stockQty + '}';
    }
}
