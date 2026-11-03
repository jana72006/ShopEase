package com.example.lavenderstore;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AboutController {

    @FXML
    private Button backToShopBtn; // التأكد إن الاسم ده يطابق الـ fx:id في ملف about-view.fxml

    @FXML
    public void initialize() {
        // حركة التكبير واللون لزرار العودة
        backToShopBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), backToShopBtn);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
            backToShopBtn.setStyle("-fx-background-color: #4A3D62; -fx-text-fill: white; -fx-background-radius: 25; -fx-padding: 10 30; -fx-font-weight: bold; -fx-cursor: hand;");
        });

        backToShopBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), backToShopBtn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
            backToShopBtn.setStyle("-fx-background-color: #5D4D7A; -fx-text-fill: white; -fx-background-radius: 25; -fx-padding: 10 30; -fx-font-weight: bold;");
        });
    }

    @FXML
    private void onBackToShopClick(ActionEvent event) throws IOException {
        // 1. تحميل صفحة المنتجات
        FXMLLoader loader = new FXMLLoader(getClass().getResource("products-view.fxml"));
        Parent root = loader.load();

        // 2. الحصول على الـ Stage الحالي
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // 3. تثبيت الأبعاد الحالية عشان الشباك ميهزش أو يصغر
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();

        // 4. إنشاء الـ Scene بالأبعاد الثابتة
        Scene scene = new Scene(root, HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // 5. ربط ملف الـ CSS (خطوة جوهرية عشان الحركات والـ Hover)
        if (getClass().getResource("style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        }

        // 6. عرض الصفحة
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void onBackToHomeClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // بنسيف الأبعاد الحالية عشان الشباك ميهزش
        double width = stage.getWidth();
        double height = stage.getHeight();

        Scene scene = new Scene(root, HelloApplication.WIDTH, HelloApplication.HEIGHT); // بنثبت الأبعاد هنا

        // 3. الخطوة السحرية: ربط ملف الـ CSS بالـ Scene الجديد
        if (getClass().getResource("style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        }
        stage.setScene(scene);
        stage.show();
    }

}