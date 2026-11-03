package ShopEase;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private List<CartItem> items;
    private double totalAmount;

    public Order(int orderId) {
        this.orderId = orderId;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    public void addItem(CartItem item) {
        items.add(item);
        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (CartItem item : items) {
            totalAmount += item.getSubtotal();
        }
    }

    public int getOrderId() { return orderId; }
    public List<CartItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }

    @Override
    public String toString() {
        return "Order ID: " + orderId + " | Total Items: " + items.size() + " | Total Amount: $" + totalAmount;
    }
}
