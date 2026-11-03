package ShopEase;

import java.io.*;
import java.util.*;

public class FileManager {

    public static void saveProducts(List<Product> list, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Product p : list) {
                writer.write(
                        p.getProductId() + "|" +
                                p.getName() + "|" +
                                p.getCategory() + "|" +
                                p.getPrice() + "|" +
                                p.getStockQty() + "|" +
                                p.getImageUrl()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }

    public static List<Product> loadProducts(String filename) {
        List<Product> list = new ArrayList<>();
        File file = new File(filename);

        // التأكد من وجود الملف أولاً قبل محاولة قراءته
        if (!file.exists()) {
            System.err.println("Warning: File not found: " + filename);
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 1. تجاهل الأسطر الفارغة تماماً
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");

                // 2. التحقق من طول المصفوفة (يجب أن يكون 6 لتجنب ArrayIndexOutOfBoundsException)
                if (parts.length < 6) {
                    System.err.println("Skipping malformed line (missing data): " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String category = parts[2].trim();
                    double price = Double.parseDouble(parts[3].trim());
                    int stock = Integer.parseInt(parts[4].trim());
                    String imageUrl = parts[5].trim();

                    list.add(new Product(id, name, category, price, stock, imageUrl));
                } catch (NumberFormatException e) {
                    // 3. حماية البرنامج من البيانات غير الرقمية في خانة الـ ID أو السعر
                    System.err.println("Skipping line due to invalid number format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading products: " + e.getMessage());
        }

        return list;
    }

    public static void saveOrders(List<Order> list, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Order o : list) {
                writer.write(o.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving orders: " + e.getMessage());
        }
    }
}