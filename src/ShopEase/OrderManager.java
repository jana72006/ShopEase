package ShopEase;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private List<Product> availableProducts;
    private List<Order>   orderHistory;

    public OrderManager() {
        this.availableProducts = new ArrayList<>();
        this.orderHistory      = new ArrayList<>();
    }

    public void addProduct(Product product) {
        availableProducts.add(product);
    }

    public void placeOrder(Order order) {
        orderHistory.add(order);
    }

    public List<Product> getAvailableProducts() {
        return availableProducts;
    }

    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }
}
