//package com.example.lavenderstore;
//
//import ShopEase.*;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.effect.BoxBlur;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.*;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//import javafx.stage.Window;
//import javafx.animation.*;
//import javafx.util.Duration;
//import javafx.event.ActionEvent;
//import javafx.scene.paint.Color;
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class ProductsController {
//
//    @FXML private GridPane        productsGrid;
//    @FXML private TextField       searchBar;
//    @FXML private ComboBox<String> categoryComboBox;
//    @FXML private Button          backBtn;
//    @FXML private Button          cartBtn;
//
//    // المنتجات المعروضة — بتيجي من SharedData.catalog
//    private ObservableList<Product3> allProducts = FXCollections.observableArrayList();
//
//    // Shared objects — نفس الـ objects بتاعت Admin
//    private final ProductCatalog catalog      = SharedData.getInstance().getCatalog();
//    private final OrderManager   orderManager = SharedData.getInstance().getOrderManager();
//    private final ShoppingCart   sharedCart   = SharedData.getInstance().getCart();
//
//    @FXML
//    public void initialize() {
//        categoryComboBox.setItems(FXCollections.observableArrayList(
//                "All", "Electronics", "Books", "Clothing", "Decor"));
//        categoryComboBox.setValue("All");
//        searchBar.setPromptText("Search with aesthetic vibes...");
//
//        loadFromCatalog();
//        onCategoryChange();
//
//        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
//            if (newVal == null || newVal.trim().isEmpty()) onCategoryChange();
//            else filterBySearch(newVal.trim());
//        });
//
//        styleControls();
//    }
//
//    /** يحوّل ShopEase.Product إلى Product3 عشان الـ UI يشتغل */
//    private void loadFromCatalog() {
//        allProducts.clear();
//        for (ShopEase.Product p : catalog.getAllProducts()) {
//            allProducts.add(new Product3(
//                    p.getProductId(), p.getName(), p.getCategory(),
//                    p.getPrice(), p.getStockQty(), p.getImageUrl()));
//        }
//    }
//
//    @FXML
//    private void onCartClick(ActionEvent event) { showCartPopUp(); }
//
//    @FXML
//    private void handleCartAction() { showCartPopUp(); }
//
//    @FXML
//    private void onSearchClick() { filterBySearch(searchBar.getText()); }
//
//    private void filterBySearch(String query) {
//        String q = query.toLowerCase();
//        List<Product3> filtered = allProducts.stream()
//                .filter(p -> p.getName().toLowerCase().contains(q))
//                .collect(Collectors.toList());
//        if (filtered.isEmpty()) showNotFound();
//        else renderProducts(filtered);
//    }
//
//    @FXML
//    public void onCategoryChange() {
//        loadFromCatalog();
//        String selected = categoryComboBox.getValue();
//        if (selected == null) selected = "All";
//        final String cat = selected;
//        List<Product3> filtered = allProducts.stream()
//                .filter(p -> cat.equals("All") || p.getCategory().equals(cat))
//                .collect(Collectors.toList());
//        renderProducts(filtered);
//    }
//
//    // ── CART POPUP ────────────────────────────────────────────────
//    public void showCartPopUp() {
//        Stage popup = new Stage();
//        popup.initModality(Modality.APPLICATION_MODAL);
//        popup.initStyle(StageStyle.TRANSPARENT);
//        popup.initOwner(productsGrid.getScene().getWindow());
//
//        VBox mainLayout = new VBox(20);
//        mainLayout.setPadding(new Insets(15));
//        mainLayout.setAlignment(Pos.CENTER);
//        mainLayout.setStyle("-fx-background-color: #f3e5f5; -fx-background-radius: 20;" +
//                "-fx-border-radius: 20; -fx-border-color: #5D4D7A; -fx-border-width: 1;");
//
//        HBox closeRow = new HBox();
//        closeRow.setAlignment(Pos.TOP_RIGHT);
//        Button xBtn = new Button("✕");
//        xBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #5D4D7A;" +
//                "-fx-font-size: 18; -fx-cursor: hand; -fx-font-weight: bold;");
//        xBtn.setOnAction(e -> popup.close());
//        closeRow.getChildren().add(xBtn);
//
//        VBox innerCard = new VBox(15);
//        innerCard.setPadding(new Insets(25));
//        innerCard.setAlignment(Pos.TOP_CENTER);
//        innerCard.setStyle("-fx-background-color: #fffaf1; -fx-background-radius: 20;");
//        VBox.setVgrow(innerCard, Priority.ALWAYS);
//
//        Label header = new Label("Items in your bag:");
//        header.setStyle("-fx-font-size: 26; -fx-font-weight: bold;" +
//                "-fx-text-fill: #3e3352; -fx-font-family: 'Georgia';");
//
//        Label totalLabel = new Label();
//        totalLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #5D4D7A;");
//        Runnable updateTotal = () -> {
//            double t = CartManager.getItems().stream()
//                    .mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
//            totalLabel.setText("Total: $" + String.format("%.2f", t));
//        };
//        updateTotal.run();
//
//        // تم التعديل هنا لاستخدام Product3 بدلاً من Product
//        ListView<com.example.lavenderstore.Product3> listView =
//                new ListView<>(CartManager.getItems());
//        listView.setPrefHeight(250);
//        listView.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
//        listView.setCellFactory(param -> new ListCell<com.example.lavenderstore.Product3>() {
//            @Override
//            protected void updateItem(com.example.lavenderstore.Product3 item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null) {
//                    setGraphic(null); setText(null);
//                    setStyle("-fx-background-color: transparent;");
//                } else {
//                    HBox cell = new HBox(5);
//                    cell.setAlignment(Pos.CENTER_LEFT);
//                    cell.setPadding(new Insets(5));
//                    cell.setStyle("-fx-background-color: transparent;" +
//                            "-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
//                    Label name = new Label(item.getName() + " (x" + item.getQuantity() + ")");
//                    name.setStyle("-fx-font-size: 15; -fx-text-fill: #444444;");
//                    name.prefWidthProperty().bind(listView.widthProperty().subtract(150));
//                    Region spacer = new Region();
//                    HBox.setHgrow(spacer, Priority.ALWAYS);
//                    Label price = new Label("$" + String.format("%.2f",
//                            item.getPrice() * item.getQuantity()));
//                    price.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #5D4D7A;");
//                    Button del = new Button("🗑");
//                    del.setStyle("-fx-background-color: transparent;" +
//                            "-fx-text-fill: #C02942; -fx-font-size: 18; -fx-cursor: hand;");
//                    del.setOnAction(e -> {
//                        CartManager.removeItem(item);
//                        updateTotal.run();
//                        listView.refresh();
//                        if (CartManager.getItems().isEmpty()) popup.close();
//                    });
//                    cell.getChildren().addAll(name, spacer, price, del);
//                    cell.prefWidthProperty().bind(listView.widthProperty().subtract(20));
//                    setGraphic(cell);
//                    setStyle("-fx-background-color: transparent;");
//                }
//            }
//        });
//
//        Button damageBtn = new Button("Do the Damage 💸");
//        damageBtn.setStyle("-fx-background-color: #5D4D7A; -fx-text-fill: white;" +
//                "-fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 12 40; -fx-cursor: hand;");
//        damageBtn.setOnAction(e -> {
//            if (CartManager.getItems().isEmpty()) return;
//            popup.close();
//            placeOrder();
//            CartManager.clear();
//            showDramaticMessage();
//        });
//
//        innerCard.getChildren().addAll(header, listView);
//        mainLayout.getChildren().addAll(closeRow, innerCard, totalLabel, damageBtn);
//
//        javafx.scene.Scene sc = new javafx.scene.Scene(mainLayout, 450, 600);
//        sc.setFill(null);
//        popup.setScene(sc);
//        popup.show();
//    }
//
//    private void placeOrder() {
//        try {
//            // تم التعديل هنا لاستخدام Product3
//            for (com.example.lavenderstore.Product3 fxP : CartManager.getItems()) {
//                for (ShopEase.Product swP : catalog.getAllProducts()) {
//                    if (swP.getName().equals(fxP.getName())) {
//                        sharedCart.addItem(swP, fxP.getQuantity());
//                        break;
//                    }
//                }
//            }
//            int orderId = orderManager.getOrderHistory().size() + 1;
//            Order order = sharedCart.checkout(orderId);
//            orderManager.placeOrder(order);
//            FileManager.saveOrders(orderManager.getOrderHistory(), "data/orders.txt");
//            FileManager.saveProducts(catalog.getAllProducts(), "data/products.txt");
//        } catch (OutOfStockException | CartEmptyException ignored) {}
//    }
//
//    // ── DISPLAY HELPERS ──────────────────────────────────────────
//    private void renderProducts(List<Product3> list) {
//        if (productsGrid == null) return;
//        productsGrid.getChildren().clear();
//        List<Product3> shuffled = new ArrayList<>(list);
//        Collections.shuffle(shuffled);
//        List<Product3> toShow = shuffled.stream().limit(25).collect(Collectors.toList());
//        int col = 0, row = 0;
//        for (Product3 p : toShow) {
//            productsGrid.add(createCard(p), col, row);
//            if (++col == 5) { col = 0; row++; }
//        }
//    }
//
//    private void showNotFound() {
//        productsGrid.getChildren().clear();
//        VBox box = new VBox(15);
//        box.setAlignment(Pos.CENTER);
//        box.setPadding(new Insets(100, 0, 0, 0));
//        Label err = new Label("Oops! No items found. 💜");
//        err.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: #5D4D7A;");
//        Label sub = new Label("Try searching for something else.");
//        sub.setStyle("-fx-font-size: 16; -fx-text-fill: #777777;");
//        box.getChildren().addAll(err, sub);
//        productsGrid.add(box, 0, 0, 5, 1);
//    }
//
//    private VBox createCard(Product3 p) {
//        VBox card = new VBox(10);
//        card.setAlignment(Pos.CENTER);
//        card.setMinWidth(150);
//        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
//                "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.1),10,0,0,5);" +
//                "-fx-padding: 15; -fx-cursor: hand;");
//
//        ImageView iv = new ImageView(new Image(p.getImageUrl(), true));
//        iv.setFitWidth(80);
//        iv.setPreserveRatio(true);
//
//        Label nameLbl = new Label(p.getName());
//        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4b3d8e;");
//        nameLbl.setWrapText(true);
//        nameLbl.setMaxWidth(130);
//
//        Label priceLbl = new Label("$" + p.getPrice());
//        priceLbl.setStyle("-fx-text-fill: #8e7cc3;");
//
//        card.getChildren().addAll(iv, nameLbl, priceLbl);
//
//        if (p.getStockQty() > 0 && p.getStockQty() < 5) {
//            Label warn = new Label("Only " + p.getStockQty() + " left!");
//            warn.setStyle("-fx-font-size: 10; -fx-text-fill: #e94560; -fx-font-weight: bold;");
//            card.getChildren().add(warn);
//        } else if (p.getStockQty() == 0) {
//            Label out = new Label("Out of Stock");
//            out.setStyle("-fx-font-size: 10; -fx-text-fill: #999; -fx-font-weight: bold;");
//            card.getChildren().add(out);
//        }
//
//        card.setOnMouseClicked(e -> showDetails(p));
//        return card;
//    }
//
//    private void showDetails(Product3 p) {
//        productsGrid.getChildren().clear();
//        productsGrid.getRowConstraints().clear();
//        productsGrid.getColumnConstraints().clear();
//
//        try {
//            VBox main = (VBox) productsGrid.getScene().getRoot();
//            Node sb = main.getChildren().get(0);
//            sb.setVisible(false); sb.setManaged(false);
//        } catch (Exception ignored) {}
//
//        HBox content = new HBox(60);
//        content.setAlignment(Pos.CENTER);
//        content.setPadding(new Insets(40));
//
//        ImageView img = new ImageView(new Image(p.getImageUrl(), true));
//        img.setFitWidth(400);
//        img.setPreserveRatio(true);
//
//        VBox info = new VBox(20);
//        info.setAlignment(Pos.CENTER_LEFT);
//
//        Label title = new Label(p.getName());
//        title.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #2d2d2d;");
//        Label cat = new Label("Category: " + p.getCategory());
//        cat.setStyle("-fx-font-size: 18; -fx-text-fill: #777777;");
//        Label price = new Label("$" + p.getPrice());
//        price.setStyle("-fx-font-size: 28; -fx-text-fill: #5D4D7A; -fx-font-weight: bold;");
//        Label stock = new Label("Stock: " + p.getStockQty());
//        stock.setStyle("-fx-font-size: 14; -fx-text-fill:" +
//                (p.getStockQty() < 5 ? " #e94560;" : " #2ecc71;"));
//
//        Spinner<Integer> qty = new Spinner<>(1, Math.max(1, p.getStockQty()), 1);
//        qty.setPrefWidth(80);
//
//        Button addBtn = new Button("ADD TO CART");
//        addBtn.setPrefWidth(220);
//        addBtn.setStyle("-fx-background-color: #5D4D7A; -fx-text-fill: white;" +
//                "-fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 12; -fx-cursor: hand;");
//
//        if (p.getStockQty() == 0) {
//            addBtn.setText("Out of Stock");
//            addBtn.setDisable(true);
//            addBtn.setStyle("-fx-background-color: #999; -fx-text-fill: white;" +
//                    "-fx-background-radius: 25; -fx-padding: 12;");
//        }
//
//        addBtn.setOnAction(e -> {
//            // تم التعديل هنا لإنشاء Product3
//            com.example.lavenderstore.Product3 fxP =
//                    new com.example.lavenderstore.Product3(
//                            p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), p.getStockQty(), p.getImageUrl());
//            fxP.setQuantity(qty.getValue());
//            CartManager.addProduct(fxP);
//            addBtn.setText("ADDED x" + qty.getValue() + "! 💜");
//            addBtn.setDisable(true);
//        });
//
//        HBox actions = new HBox(15, qty, addBtn);
//        actions.setAlignment(Pos.CENTER_LEFT);
//        info.getChildren().addAll(title, cat, price, stock, actions);
//        content.getChildren().addAll(img, info);
//
//        Button back = new Button("← Back to Shop");
//        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #5D4D7A;" +
//                "-fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
//        back.setOnAction(e -> {
//            try {
//                VBox main = (VBox) productsGrid.getScene().getRoot();
//                Node sb = main.getChildren().get(0);
//                sb.setVisible(true); sb.setManaged(true);
//            } catch (Exception ignored) {}
//            onCategoryChange();
//        });
//
//        VBox layout = new VBox(30);
//        layout.setAlignment(Pos.CENTER);
//        layout.setPadding(new Insets(20));
//        layout.getChildren().addAll(new HBox(back), content);
//        productsGrid.add(layout, 0, 0, 5, 1);
//    }
//
//    private void showDramaticMessage() {
//        Stage toast = new Stage();
//        toast.initStyle(StageStyle.TRANSPARENT);
//        toast.initOwner(productsGrid.getScene().getWindow());
//        VBox root = new VBox(10);
//        root.setStyle("-fx-background-color: rgba(248,245,255,0.95);" +
//                "-fx-padding: 30; -fx-background-radius: 15;");
//        Label l1 = new Label("Bye Bye Money, Literally.");
//        l1.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #5D4D7A;");
//        Label l2 = new Label("Your wallet deserves a moment of silence. 💜");
//        l2.setStyle("-fx-font-size: 14; -fx-text-fill: #777;");
//        root.getChildren().addAll(l1, l2);
//        javafx.scene.Scene sc = new javafx.scene.Scene(root);
//        sc.setFill(null);
//        try {
//            if (getClass().getResource("style.css") != null)
//                sc.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
//        } catch (Exception ignored) {}
//        toast.setScene(sc);
//
//        Node mainRoot = productsGrid.getScene().getRoot();
//        BoxBlur blur = new BoxBlur(0, 0, 3);
//        mainRoot.setEffect(blur);
//        Timeline blurIn  = new Timeline(new KeyFrame(Duration.seconds(2),
//                new KeyValue(blur.widthProperty(), 10), new KeyValue(blur.heightProperty(), 10)));
//        Timeline blurOut = new Timeline(new KeyFrame(Duration.seconds(2),
//                new KeyValue(blur.widthProperty(), 0), new KeyValue(blur.heightProperty(), 0)));
//        blurOut.setDelay(Duration.seconds(8));
//        blurOut.setOnFinished(e -> mainRoot.setEffect(null));
//
//        toast.show();
//        Window owner = toast.getOwner();
//        toast.setX(owner.getX() + owner.getWidth()/2 - 200);
//        toast.setY(owner.getY() + owner.getHeight()/2 - 60);
//
//        FadeTransition fi = new FadeTransition(Duration.seconds(2), root);
//        fi.setFromValue(0); fi.setToValue(1);
//        FadeTransition fo = new FadeTransition(Duration.seconds(2), root);
//        fo.setFromValue(1); fo.setToValue(0);
//        fo.setDelay(Duration.seconds(8));
//        fo.setOnFinished(e -> toast.close());
//        fi.play(); blurIn.play(); fo.play(); blurOut.play();
//    }
//
//    @FXML
//    private void onBackToHomeClick(ActionEvent event) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
//        Parent root = loader.load();
//        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
//        javafx.scene.Scene scene =
//                new javafx.scene.Scene(root, HelloApplication.WIDTH, HelloApplication.HEIGHT);
//        if (getClass().getResource("style.css") != null)
//            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    private void styleControls() {
//        categoryComboBox.setStyle(
//                "-fx-background-color: #f3e5f5; -fx-background-radius: 20;" +
//                        "-fx-border-color: #5D4D7A; -fx-border-radius: 20;" +
//                        "-fx-text-fill: #5D4D7A; -fx-cursor: hand;");
//        categoryComboBox.setCellFactory(lv -> new ListCell<String>() {
//            @Override protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null) { setText(null); }
//                else {
//                    setText(item);
//                    setStyle("-fx-background-color: #f3e5f5; -fx-text-fill: #5D4D7A; -fx-padding: 8;");
//                    setOnMouseEntered(e ->
//                            setStyle("-fx-background-color: #e1bee7; -fx-text-fill: #5D4D7A; -fx-padding: 8;"));
//                    setOnMouseExited(e ->
//                            setStyle("-fx-background-color: #f3e5f5; -fx-text-fill: #5D4D7A; -fx-padding: 8;"));
//                }
//            }
//        });
//        if (cartBtn != null)
//            cartBtn.setStyle("-fx-background-color: #5D4D7A; -fx-background-radius: 25;" +
//                    "-fx-padding: 10 25; -fx-font-size: 15px; -fx-text-fill: white;");
//    }
//}

package com.example.lavenderstore;

import ShopEase.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ProductsController {

    @FXML private GridPane        productsGrid;
    @FXML private TextField       searchBar;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Button          backBtn;
    @FXML private Button          cartBtn;

    // المنتجات المعروضة — بتيجي من SharedData.catalog
    private ObservableList<Product3> allProducts = FXCollections.observableArrayList();

    // Shared objects — نفس الـ objects بتاعت Admin
    private final ProductCatalog catalog      = SharedData.getInstance().getCatalog();
    private final OrderManager   orderManager = SharedData.getInstance().getOrderManager();
    private final ShoppingCart   sharedCart   = SharedData.getInstance().getCart();

    @FXML
    public void initialize() {
        categoryComboBox.setItems(FXCollections.observableArrayList(
                "All", "Electronics", "Books", "Clothing", "Decor"));
        categoryComboBox.setValue("All");
        searchBar.setPromptText("Search with aesthetic vibes...");

        loadFromCatalog();
        onCategoryChange();

        searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) onCategoryChange();
            else filterBySearch(newVal.trim());
        });

        styleControls();
    }

    /** يحوّل ShopEase.Product إلى Product3 عشان الـ UI يشتغل */
    private void loadFromCatalog() {
        allProducts.clear();
        for (ShopEase.Product p : catalog.getAllProducts()) {
            allProducts.add(new Product3(
                    p.getProductId(), p.getName(), p.getCategory(),
                    p.getPrice(), p.getStockQty(), p.getImageUrl()));
        }
    }

    @FXML
    private void onCartClick(ActionEvent event) { showCartPopUp(); }

    @FXML
    private void handleCartAction() { showCartPopUp(); }

    @FXML
    private void onSearchClick() { filterBySearch(searchBar.getText()); }

    private void filterBySearch(String query) {
        String q = query.toLowerCase();
        List<Product3> filtered = allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
        if (filtered.isEmpty()) showNotFound();
        else renderProducts(filtered);
    }

    @FXML
    public void onCategoryChange() {
        loadFromCatalog();
        String selected = categoryComboBox.getValue();
        if (selected == null) selected = "All";
        final String cat = selected;
        List<Product3> filtered = allProducts.stream()
                .filter(p -> cat.equals("All") || p.getCategory().equals(cat))
                .collect(Collectors.toList());
        renderProducts(filtered);
    }

    // ── CART POPUP ────────────────────────────────────────────────
    public void showCartPopUp() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.initOwner(productsGrid.getScene().getWindow());

        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(15));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #f3e5f5; -fx-background-radius: 20;" +
                "-fx-border-radius: 20; -fx-border-color: #5D4D7A; -fx-border-width: 1;");

        HBox closeRow = new HBox();
        closeRow.setAlignment(Pos.TOP_RIGHT);
        Button xBtn = new Button("✕");
        xBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #5D4D7A;" +
                "-fx-font-size: 18; -fx-cursor: hand; -fx-font-weight: bold;");
        xBtn.setOnAction(e -> popup.close());
        closeRow.getChildren().add(xBtn);

        VBox innerCard = new VBox(15);
        innerCard.setPadding(new Insets(25));
        innerCard.setAlignment(Pos.TOP_CENTER);
        innerCard.setStyle("-fx-background-color: #fffaf1; -fx-background-radius: 20;");
        VBox.setVgrow(innerCard, Priority.ALWAYS);

        Label header = new Label("Items in your bag:");
        header.setStyle("-fx-font-size: 26; -fx-font-weight: bold;" +
                "-fx-text-fill: #3e3352; -fx-font-family: 'Georgia';");

        Label totalLabel = new Label();
        totalLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #5D4D7A;");
        Runnable updateTotal = () -> {
            double t = CartManager.getItems().stream()
                    .mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
            totalLabel.setText("Total: $" + String.format("%.2f", t));
        };
        updateTotal.run();

        // تم التعديل هنا لاستخدام Product3 بدلاً من Product
        ListView<com.example.lavenderstore.Product3> listView =
                new ListView<>(CartManager.getItems());
        listView.setPrefHeight(250);
        listView.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        listView.setCellFactory(param -> new ListCell<com.example.lavenderstore.Product3>() {
            @Override
            protected void updateItem(com.example.lavenderstore.Product3 item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null); setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    HBox cell = new HBox(5);
                    cell.setAlignment(Pos.CENTER_LEFT);
                    cell.setPadding(new Insets(5));
                    cell.setStyle("-fx-background-color: transparent;" +
                            "-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                    Label name = new Label(item.getName() + " (x" + item.getQuantity() + ")");
                    name.setStyle("-fx-font-size: 15; -fx-text-fill: #444444;");
                    name.prefWidthProperty().bind(listView.widthProperty().subtract(150));
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    Label price = new Label("$" + String.format("%.2f",
                            item.getPrice() * item.getQuantity()));
                    price.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #5D4D7A;");
                    Button del = new Button("🗑");
                    del.setStyle("-fx-background-color: transparent;" +
                            "-fx-text-fill: #C02942; -fx-font-size: 18; -fx-cursor: hand;");
                    del.setOnAction(e -> {
                        CartManager.removeItem(item);
                        updateTotal.run();
                        listView.refresh();
                        if (CartManager.getItems().isEmpty()) popup.close();
                    });
                    cell.getChildren().addAll(name, spacer, price, del);
                    cell.prefWidthProperty().bind(listView.widthProperty().subtract(20));
                    setGraphic(cell);
                    setStyle("-fx-background-color: transparent;");
                }
            }
        });

        Button damageBtn = new Button("Do the Damage 💸");
        damageBtn.setStyle("-fx-background-color: #5D4D7A; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 12 40; -fx-cursor: hand;");
        damageBtn.setOnAction(e -> {
            if (CartManager.getItems().isEmpty()) return;
            popup.close();
            placeOrder();
            CartManager.clear();
            showDramaticMessage();
        });

        innerCard.getChildren().addAll(header, listView);
        mainLayout.getChildren().addAll(closeRow, innerCard, totalLabel, damageBtn);

        javafx.scene.Scene sc = new javafx.scene.Scene(mainLayout, 450, 600);
        sc.setFill(null);
        popup.setScene(sc);
        popup.show();
    }

    private void placeOrder() {
        try {
            // تم التعديل هنا لاستخدام Product3
            for (com.example.lavenderstore.Product3 fxP : CartManager.getItems()) {
                for (ShopEase.Product swP : catalog.getAllProducts()) {
                    if (swP.getName().equals(fxP.getName())) {
                        sharedCart.addItem(swP, fxP.getQuantity());
                        break;
                    }
                }
            }
            int orderId = orderManager.getOrderHistory().size() + 1;
            Order order = sharedCart.checkout(orderId);
            orderManager.placeOrder(order);
            FileManager.saveOrders(orderManager.getOrderHistory(), "data/orders.txt");
            FileManager.saveProducts(catalog.getAllProducts(), "data/products.txt");
        } catch (OutOfStockException | CartEmptyException ignored) {}
    }

    // ── DISPLAY HELPERS ──────────────────────────────────────────
    private void renderProducts(List<Product3> list) {
        if (productsGrid == null) return;
        productsGrid.getChildren().clear();
        List<Product3> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled);
        List<Product3> toShow = shuffled.stream().limit(25).collect(Collectors.toList());
        int col = 0, row = 0;
        for (Product3 p : toShow) {
            productsGrid.add(createCard(p), col, row);
            if (++col == 5) { col = 0; row++; }
        }
    }

    private void showNotFound() {
        productsGrid.getChildren().clear();
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(100, 0, 0, 0));
        Label err = new Label("Oops! No items found. 💜");
        err.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: #5D4D7A;");
        Label sub = new Label("Try searching for something else.");
        sub.setStyle("-fx-font-size: 16; -fx-text-fill: #777777;");
        box.getChildren().addAll(err, sub);
        productsGrid.add(box, 0, 0, 5, 1);
    }

    private VBox createCard(Product3 p) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setMinWidth(150);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15;" +
                "-fx-effect: dropshadow(three-pass-box,rgba(0,0,0,0.1),10,0,0,5);" +
                "-fx-padding: 15; -fx-cursor: hand;");

        ImageView iv = new ImageView(new Image(p.getImageUrl(), true));
        iv.setFitWidth(80);
        iv.setPreserveRatio(true);

        Label nameLbl = new Label(p.getName());
        nameLbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #4b3d8e;");
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(130);

        Label priceLbl = new Label("$" + p.getPrice());
        priceLbl.setStyle("-fx-text-fill: #8e7cc3;");

        card.getChildren().addAll(iv, nameLbl, priceLbl);

        if (p.getStockQty() > 0 && p.getStockQty() < 5) {
            Label warn = new Label("Only " + p.getStockQty() + " left!");
            warn.setStyle("-fx-font-size: 10; -fx-text-fill: #e94560; -fx-font-weight: bold;");
            card.getChildren().add(warn);
        } else if (p.getStockQty() == 0) {
            Label out = new Label("Out of Stock");
            out.setStyle("-fx-font-size: 10; -fx-text-fill: #999; -fx-font-weight: bold;");
            card.getChildren().add(out);
        }

        card.setOnMouseClicked(e -> showDetails(p));
        return card;
    }

    private void showDetails(Product3 p) {
        productsGrid.getChildren().clear();
        productsGrid.getRowConstraints().clear();
        productsGrid.getColumnConstraints().clear();

        try {
            VBox main = (VBox) productsGrid.getScene().getRoot();
            Node sb = main.getChildren().get(0);
            sb.setVisible(false); sb.setManaged(false);
        } catch (Exception ignored) {}

        HBox content = new HBox(60);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        ImageView img = new ImageView(new Image(p.getImageUrl(), true));
        img.setFitWidth(400);
        img.setPreserveRatio(true);

        VBox info = new VBox(20);
        info.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(p.getName());
        title.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #2d2d2d;");
        Label cat = new Label("Category: " + p.getCategory());
        cat.setStyle("-fx-font-size: 18; -fx-text-fill: #777777;");
        Label price = new Label("$" + p.getPrice());
        price.setStyle("-fx-font-size: 28; -fx-text-fill: #5D4D7A; -fx-font-weight: bold;");
        Label stock = new Label("Stock: " + p.getStockQty());
        stock.setStyle("-fx-font-size: 14; -fx-text-fill:" +
                (p.getStockQty() < 5 ? " #e94560;" : " #2ecc71;"));

        Spinner<Integer> qty = new Spinner<>(1, Math.max(1, p.getStockQty()), 1);
        qty.setPrefWidth(80);

        Button addBtn = new Button("ADD TO CART");
        addBtn.setPrefWidth(220);
        addBtn.setStyle("-fx-background-color: #5D4D7A; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-background-radius: 25; -fx-padding: 12; -fx-cursor: hand;");

        if (p.getStockQty() == 0) {
            addBtn.setText("Out of Stock");
            addBtn.setDisable(true);
            addBtn.setStyle("-fx-background-color: #999; -fx-text-fill: white;" +
                    "-fx-background-radius: 25; -fx-padding: 12;");
        }

        addBtn.setOnAction(e -> {
            // تم التعديل هنا لإنشاء Product3
            com.example.lavenderstore.Product3 fxP =
                    new com.example.lavenderstore.Product3(
                            p.getProductId(), p.getName(), p.getCategory(), p.getPrice(), p.getStockQty(), p.getImageUrl());
            fxP.setQuantity(qty.getValue());
            CartManager.addProduct(fxP);
            addBtn.setText("ADDED x" + qty.getValue() + "! 💜");
            addBtn.setDisable(true);
        });

        HBox actions = new HBox(15, qty, addBtn);
        actions.setAlignment(Pos.CENTER_LEFT);
        info.getChildren().addAll(title, cat, price, stock, actions);
        content.getChildren().addAll(img, info);

        Button back = new Button("← Back to Shop");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #5D4D7A;" +
                "-fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        back.setOnAction(e -> {
            try {
                VBox main = (VBox) productsGrid.getScene().getRoot();
                Node sb = main.getChildren().get(0);
                sb.setVisible(true); sb.setManaged(true);
            } catch (Exception ignored) {}
            onCategoryChange();
        });

        VBox layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(new HBox(back), content);
        productsGrid.add(layout, 0, 0, 5, 1);
    }

    private void showDramaticMessage() {
        Stage toast = new Stage();
        toast.initStyle(StageStyle.TRANSPARENT);
        toast.initOwner(productsGrid.getScene().getWindow());
        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: rgba(248,245,255,0.95);" +
                "-fx-padding: 30; -fx-background-radius: 15;");
        Label l1 = new Label("Bye Bye Money, Literally.");
        l1.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #5D4D7A;");
        Label l2 = new Label("Your wallet deserves a moment of silence. 💜");
        l2.setStyle("-fx-font-size: 14; -fx-text-fill: #777;");
        root.getChildren().addAll(l1, l2);
        javafx.scene.Scene sc = new javafx.scene.Scene(root);
        sc.setFill(null);
        try {
            if (getClass().getResource("style.css") != null)
                sc.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        } catch (Exception ignored) {}
        toast.setScene(sc);

        Node mainRoot = productsGrid.getScene().getRoot();
        BoxBlur blur = new BoxBlur(0, 0, 3);
        mainRoot.setEffect(blur);
        Timeline blurIn  = new Timeline(new KeyFrame(Duration.seconds(2),
                new KeyValue(blur.widthProperty(), 10), new KeyValue(blur.heightProperty(), 10)));
        Timeline blurOut = new Timeline(new KeyFrame(Duration.seconds(2),
                new KeyValue(blur.widthProperty(), 0), new KeyValue(blur.heightProperty(), 0)));
        blurOut.setDelay(Duration.seconds(8));
        blurOut.setOnFinished(e -> mainRoot.setEffect(null));

        toast.show();
        Window owner = toast.getOwner();
        toast.setX(owner.getX() + owner.getWidth()/2 - 200);
        toast.setY(owner.getY() + owner.getHeight()/2 - 60);

        FadeTransition fi = new FadeTransition(Duration.seconds(2), root);
        fi.setFromValue(0); fi.setToValue(1);
        FadeTransition fo = new FadeTransition(Duration.seconds(2), root);
        fo.setFromValue(1); fo.setToValue(0);
        fo.setDelay(Duration.seconds(8));
        fo.setOnFinished(e -> toast.close());
        fi.play(); blurIn.play(); fo.play(); blurOut.play();
    }

    @FXML
    private void onBackToHomeClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        javafx.scene.Scene scene =
                new javafx.scene.Scene(root, HelloApplication.WIDTH, HelloApplication.HEIGHT);
        if (getClass().getResource("style.css") != null)
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void styleControls() {
        categoryComboBox.setStyle(
                "-fx-background-color: #f3e5f5; -fx-background-radius: 20;" +
                        "-fx-border-color: #5D4D7A; -fx-border-radius: 20;" +
                        "-fx-text-fill: #5D4D7A; -fx-cursor: hand;");
        categoryComboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else {
                    setText(item);
                    setStyle("-fx-background-color: #f3e5f5; -fx-text-fill: #5D4D7A; -fx-padding: 8;");
                    setOnMouseEntered(e ->
                            setStyle("-fx-background-color: #e1bee7; -fx-text-fill: #5D4D7A; -fx-padding: 8;"));
                    setOnMouseExited(e ->
                            setStyle("-fx-background-color: #f3e5f5; -fx-text-fill: #5D4D7A; -fx-padding: 8;"));
                }
            }
        });
        if (cartBtn != null)
            cartBtn.setStyle("-fx-background-color: #5D4D7A; -fx-background-radius: 25;" +
                    "-fx-padding: 10 25; -fx-font-size: 15px; -fx-text-fill: white;");
    }}
