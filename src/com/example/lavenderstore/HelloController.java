package com.example.lavenderstore;

import ShopEase.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloController {

    @FXML private ImageView mainImage;
    @FXML private Region    overlay;
    @FXML private ImageView fbIcon;
    @FXML private ImageView instaIcon;

    @FXML
    public void initialize() {
        if (mainImage != null) {
            Platform.runLater(() -> {
                if (mainImage.getScene() != null) {
                    mainImage.fitWidthProperty().bind(
                        mainImage.getScene().widthProperty());
                    mainImage.fitHeightProperty().bind(
                        mainImage.getScene().heightProperty());
                }
                mainImage.setPreserveRatio(false);
            });
        }
    }

    @FXML
    private void onAboutClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("about-view.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, HelloApplication.WIDTH, HelloApplication.HEIGHT));
        stage.show();
    }

    @FXML
    private void onCartClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("cart-view.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, HelloApplication.WIDTH, HelloApplication.HEIGHT));
        stage.show();
    }

    @FXML
    protected void onShopNowClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("products-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainImage.getScene().getWindow();
            Scene scene = new Scene(root, HelloApplication.WIDTH, HelloApplication.HEIGHT);
            if (getClass().getResource("style.css") != null)
                scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** زرار Admin — بيفتح Swing AdminLoginDialog */
    @FXML
    private void onAdminClick(ActionEvent event) {
        Main.openAdminLogin();
    }

    @FXML private void onFBClick()    {}
    @FXML private void onInstaClick() {}
}
