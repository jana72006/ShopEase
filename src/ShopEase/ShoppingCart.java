package ShopEase;

import java.util.*;

public class ShoppingCart {

    private ArrayList<CartItem> items;
    private static final double TAX_RATE = 0.14;

    public ShoppingCart() {
        items = new ArrayList<>();
    }

    public void addItem(Product p, int qty) throws OutOfStockException {
        if (p.getStockQty() < qty) {
            throw new OutOfStockException("Not enough stock!");
        }

        for (CartItem item : items) {
            if (item.getProduct().getProductId() == p.getProductId()) {
                item.setQuantity(item.getQuantity() + qty);
                return;
            }
        }

        items.add(new CartItem(p, qty));
    }

    public void removeItem(int productId) {
        items.removeIf(item -> item.getProduct().getProductId() == productId);
    }

    public void clearCart() {
        items.clear();
    }

    public double calculateSubtotal() {
        double total = 0;

        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }

        return total;
    }

    public double calculateTax() {
        return calculateSubtotal() * TAX_RATE;
    }

    public double calculateTotal() {
        return calculateSubtotal() + calculateTax();
    }

    public int getTotalItems() {
        int total = 0;

        for (CartItem item : items) {
            total += item.getQuantity();
        }

        return total;
    }

    public Order checkout(int orderId) throws CartEmptyException {

        if (items.isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        Order order = new Order(orderId);

        for (CartItem item : items) {

            Product p = item.getProduct();

            p.setStockQty(p.getStockQty() - item.getQuantity());

            order.addItem(item);
        }

        clearCart();

        return order;
    }
}
