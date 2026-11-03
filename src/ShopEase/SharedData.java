package ShopEase;

/**
 * SharedData — Singleton
 * كل الـ objects المشتركة بين Admin (Swing) وCustomer (JavaFX)
 * بتعيش هنا وبتتعمل مرة واحدة بس
 */
public class SharedData {

    private static SharedData instance;

    private final ProductCatalog catalog;
    private final OrderManager   orderManager;
    private final ShoppingCart   cart;

    private SharedData() {
        this.catalog      = new ProductCatalog();
        this.orderManager = new OrderManager();
        this.cart         = new ShoppingCart();
    }

    public static synchronized SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public ProductCatalog getCatalog()      { return catalog;       }
    public OrderManager   getOrderManager() { return orderManager;  }
    public ShoppingCart   getCart()         { return cart;          }
}
