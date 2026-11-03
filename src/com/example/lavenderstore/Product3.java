package com.example.lavenderstore;

/**
 * Product3 — Wrapper بيحمل بيانات ShopEase.Product
 * ويضيف quantity للـ cart UI بتاع Student 3
 */
public class Product3 {
    private final int    productId;
    private final String name;
    private final String category;
    private final double price;
    private       int    stockQty;
    private final String imageUrl;
    private       int    quantity = 1;

    public Product3(int productId, String name, String category,
                    double price, int stockQty, String imageUrl) {
        this.productId = productId;
        this.name      = name;
        this.category  = category;
        this.price     = price;
        this.stockQty  = stockQty;
        this.imageUrl  = imageUrl;
    }

    public int    getProductId() { return productId; }
    public String getName()      { return name;      }
    public String getCategory()  { return category;  }
    public double getPrice()     { return price;     }
    public int    getStockQty()  { return stockQty;  }
    public String getImageUrl()  { return imageUrl;  }
    public int    getQuantity()  { return quantity;  }
    public void   setQuantity(int q) { this.quantity = q; }
    public void   setStockQty(int q) { this.stockQty = q; }
}
