//package com.example.lavenderstore;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//
//public class CartManager {
//    // دي القائمة اللي هتفضل شايلة المنتجات طول ما الأبلكيشن شغال
//    private static final ObservableList<Product> items = FXCollections.observableArrayList();
//
//    public static ObservableList<Product> getItems() {
//        return items;
//    }
//
//    public static void addProduct(Product product) {
//        items.add(product);
//    }
//
//    // --- ضيفي الميثود دي عشان المسح يشتغل ---
//    public static void removeProduct(Product product) {
//        items.remove(product);
//    }
//
//    public static double getTotalPrice() {
//        double total = 0;
//        for (Product p : items) {
//            total += (p.getPrice() * p.getQuantity()); // السعر × الكمية
//        }
//        return total;
//    }
//
//
//    public static void addItem(Product product) {
//    }
//
//    public static void removeItem(Product item) {
//        items.remove(item);
//    }
//
//    public static void clear() {
//    }
//}

package com.example.lavenderstore;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CartManager {
    // استخدمنا Product3 بدلاً من Product عشان نربط مع النسخة الجديدة
    private static final ObservableList<Product3> items = FXCollections.observableArrayList();

    public static ObservableList<Product3> getItems() {
        return items;
    }

    public static void addProduct(Product3 product) {
        items.add(product);
    }

    public static void removeProduct(Product3 product) {
        items.remove(product);
    }

    public static double getTotalPrice() {
        double total = 0;
        for (Product3 p : items) {
            // اتأكدي إن Product3 فيها getPrice و getQuantity
            total += (p.getPrice() * p.getQuantity());
        }
        return total;
    }

    public static void addItem(Product3 product) {
        addProduct(product);
    }

    public static void removeItem(Product3 item) {
        items.remove(item);
    }

    public static void clear() {
        items.clear();
    }
}