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

public class CartController {

    @FXML
    private Button buyAndCryBtn; // لازم الاسم ده يطابق الـ fx:id اللي في الـ FXML

    @FXML
    public void initialize() {
        // حركة التكبير لما الماوس يروح على الزرار
        buyAndCryBtn.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), buyAndCryBtn);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
            buyAndCryBtn.setStyle("-fx-background-color: #4A3D62; -fx-text-fill: white; -fx-background-radius: 25; -fx-padding: 12 40; -fx-font-weight: bold; -fx-cursor: hand;");
        });

        // يرجع لحجمه الطبيعي لما الماوس يبعد
        buyAndCryBtn.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), buyAndCryBtn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
            buyAndCryBtn.setStyle("-fx-background-color: #5D4D7A; -fx-text-fill: white; -fx-background-radius: 25; -fx-padding: 12 40; -fx-font-weight: bold;");
        });
    }

    @FXML
    private void onBackToShopClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("products-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, HelloApplication.WIDTH, HelloApplication.HEIGHT);

        // ربط ملف الـ CSS عشان الستايل والحركات تشتغل
        if (getClass().getResource("style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}