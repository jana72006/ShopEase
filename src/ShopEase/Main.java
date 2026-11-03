package ShopEase;

import com.example.lavenderstore.HelloApplication;
import javafx.application.Application;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * Main — نقطة البداية الوحيدة للبروجكت كله
 *
 * 1. يحمّل products.txt في SharedData.catalog
 * 2. يفتح JavaFX Customer UI
 * 3. زرار Admin يفتح AdminLoginDialog (Swing)
 * 4. لو login صح يفتح AdminFrame (Swing) بنفس الـ objects
 */
public class Main {

    public static void main(String[] args) {
        loadSharedData();
        Application.launch(HelloApplication.class, args);
    }

    public static void loadSharedData() {
        ProductCatalog catalog = SharedData.getInstance().getCatalog();
        File file = new File("data/products.txt");
        if (file.exists()) {
            List<Product> loaded = FileManager.loadProducts("data/products.txt");
            for (Product p : loaded) {
                try { catalog.addProduct(p); }
                catch (DuplicateProductException ignored) {}
            }
        }
    }

    /**
     * بيتنادى من HelloController لما يضغط زرار Admin
     * بيشتغل على Swing EDT
     */
    public static void openAdminLogin() {
        SwingUtilities.invokeLater(() -> {
            JFrame dummy = new JFrame();
            dummy.setUndecorated(true);
            dummy.setVisible(true);
            dummy.setLocationRelativeTo(null);

            AdminLoginDialog login = new AdminLoginDialog(dummy);
            login.setVisible(true);
            dummy.dispose();

            if (login.isLoginSuccess()) {
                new AdminFrame(
                    SharedData.getInstance().getCatalog(),
                    SharedData.getInstance().getOrderManager()
                );
            }
        });
    }
}
