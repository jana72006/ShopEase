package com.example.lavenderstore;

import ShopEase.SharedData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {

    public static final double WIDTH  = 1000;
    public static final double HEIGHT = 700;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
        stage.setTitle("ShopEase — Store");
        stage.setResizable(true);
        stage.centerOnScreen();
        try {
            scene.getStylesheets().add(
                Objects.requireNonNull(
                    getClass().getResource("style.css")).toExternalForm());
        } catch (NullPointerException ignored) {}
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
