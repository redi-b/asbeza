package org.asbeza.frontend.controllers.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.CartService;
import org.asbeza.frontend.services.ProductService;
import org.asbeza.frontend.types.Product;
import org.asbeza.frontend.responses.ProductResponse;
import org.asbeza.frontend.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BrowseController extends CommonController {

    public TextField searchField;
    public ComboBox<String> categoryFilter;
    public Label lblUserName;
    public Label loadingLabel;
    public Label errorLabel;

    @FXML
    private VBox productsContainer;

    private final ObservableList<Product> allProducts = FXCollections.observableArrayList(); // All products
    private final ObservableList<Product> filteredProducts = FXCollections.observableArrayList(); // Filtered products

    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();
    private final List<String> categories = new ArrayList<>();

    @FXML
    public void initialize() {
        showError(false);
        showLoading(true);
        Platform.runLater(() -> {
            lblUserName.setText(session.getUserName());
            loadProducts();
        });

        // Add event listeners for search and filter
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void loadProducts() {
        try {
            @SuppressWarnings("unchecked")
            List<Product> cachedProducts = (List<Product>) cache.get("products");

            // Proceed with cached data
            if (cachedProducts != null && !cache.isExpired("products") && !cachedProducts.isEmpty()) {
                allProducts.setAll(cachedProducts);
                filteredProducts.setAll(allProducts);
                updateProductContainer();
                loadCategories();
                showLoading(false);
                return;
            }
        } catch (ClassCastException e) {
            System.err.println("Cache retrieval failed: " + e.getMessage());
            cache.remove("products");
        }

        ProductService.getAllProducts(new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ProductResponse productResponse = objectMapper.readValue(response, ProductResponse.class);

                    // Cache the fetched products
                    cache.put("products", productResponse.getData());

                    // Add products to the observable list
                    allProducts.setAll(productResponse.getData());
                    filteredProducts.setAll(allProducts); // Initially, all products are visible
                    updateProductContainer();

                    loadCategories();

                    showLoading(false);
                } catch (Exception e) {
                    loadCategories();

                    showLoading(false);
                    showError(true);
                    setErrorMessage(e.getMessage());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                showLoading(false);
                showError(true);
                setErrorMessage(errorMessage);
            }
        });
    }

    private void loadCategories() {
        categories.clear();
        categories.add("All");
        categories.addAll(
                allProducts.stream()
                        .map(Product::getCategory)  // Extract category from each product
                        .distinct()                 // Remove duplicates
                        .toList()
        );

        categoryFilter.getItems().clear();
        categoryFilter.getItems().addAll(categories);
        categoryFilter.getSelectionModel().selectFirst();  // Default to "All"
    }

    private void updateProductContainer() {
        productsContainer.getChildren().clear();

        if (filteredProducts.isEmpty()) {
            showEmptyMessage("No products found.", productsContainer);
        }

        for (Product product : filteredProducts) {
            productsContainer.getChildren().add(createProductCard(product));
        }
    }

    @FXML
    private void applyFilters() {
        String searchQuery = searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryFilter.getValue();

        filteredProducts.setAll(allProducts.stream()
                .filter(product -> (searchQuery.isEmpty() || product.getName().toLowerCase().contains(searchQuery)))
                .filter(product -> ("All".equals(selectedCategory) || product.getCategory().equalsIgnoreCase(selectedCategory)))
                .collect(Collectors.toList()));

        updateProductContainer();
    }

    private HBox createProductCard(Product product) {
        HBox card = new HBox(15);
        card.setStyle("-fx-padding: 10; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefHeight(120);

        // Product Details
        VBox details = new VBox(5);
        details.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox priceContainer = new HBox(4);
        priceContainer.setAlignment(Pos.CENTER_LEFT);

        Label priceLabel = new Label("Price: " + product.getPrice() + " Birr");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

        String per = (product.getPricePer() == null || product.getPricePer().isEmpty()) ?
                        "" :
                        "per " + product.getPricePer();
        Label pricePer = new Label(per);
        pricePer.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        priceContainer.getChildren().addAll(priceLabel, pricePer);

        Label stockLabel = new Label("In Stock: " + product.getStockQuantity());
        stockLabel.setStyle("-fx-font-size: 12px;");

        Label categoryLabel = new Label("Category: " + product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // Lazy Load Image
        ImageView productImageView = new ImageView();
        productImageView.setFitHeight(100);
        productImageView.setFitWidth(100);
        loadProductImageAsync(product.getImage(), productImageView);

        details.getChildren().addAll(nameLabel, priceContainer, stockLabel, categoryLabel);

        // Quantity Input with +/- Buttons
        HBox quantityInput = new HBox(5);
        quantityInput.setStyle("-fx-alignment: center-right;");

        Button decrementButton = new Button("-");
        decrementButton.getStyleClass().add("circular-button");

        Button incrementButton = new Button("+");
        incrementButton.getStyleClass().add("circular-button");

        TextField quantityField = new TextField("1");
        quantityField.setPrefWidth(40);
        quantityField.setStyle("-fx-padding: 6 8;");
        quantityField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d*")) ? change : null)); // Restrict to numbers

        // Update quantity on button clicks
        decrementButton.setOnAction(e -> {
            int currentQuantity = Integer.parseInt(quantityField.getText());
            if (currentQuantity > 1) {
                quantityField.setText(String.valueOf(currentQuantity - 1));
            }
        });

        incrementButton.setOnAction(e -> {
            int currentQuantity = Integer.parseInt(quantityField.getText());
            if (currentQuantity < product.getStockQuantity()) {
                quantityField.setText(String.valueOf(currentQuantity + 1));
            }
        });

        quantityInput.getChildren().addAll(decrementButton, quantityField, incrementButton);

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setPrefWidth(96);
        addToCartButton.getStyleClass().add("cart-btn");

        // Add to Cart with Specific Item and Quantity
        addToCartButton.setOnAction(e -> handleAddToCart(
                        product,
                        Integer.parseInt(quantityField.getText()),
                        addToCartButton
                )
        );

        HBox buttonContainer = new HBox(10);
        buttonContainer.setStyle("-fx-alignment: center-right; -fx-padding: 5 0 0 0;");
        buttonContainer.getChildren().addAll(quantityInput, addToCartButton);

        HBox.setHgrow(details, Priority.ALWAYS);
        card.getChildren().addAll(productImageView, details, buttonContainer);

        return card;
    }

    private void loadProductImageAsync(String imageUrl, ImageView imageView) {
        // Check if the image is already in the cache
        if (imageCache.containsKey(imageUrl)) {
            // Use the cached image
            Platform.runLater(() -> {
                imageView.setImage(imageCache.get(imageUrl));
                applyRoundedCorners(imageView);
            });
        } else {
            // Load the image asynchronously and cache it
            new Thread(() -> {
                try {
                    Image image = ImageUtil.createImage(imageUrl); // Load the image asynchronously
                    imageCache.put(imageUrl, image); // Store the image in the cache
                    Platform.runLater(() -> {
                        imageView.setImage(image);
                        applyRoundedCorners(imageView);
                    });
                } catch (Exception e) {
                    System.err.println("Failed to load image: " + imageUrl + " Error: " + e.getMessage());
                }
            }).start();
        }
    }

    private void applyRoundedCorners(ImageView imageView) {
        double radius = 10; // Set the corner radius
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(radius);
        clip.setArcHeight(radius);
        imageView.setClip(clip);
    }

    private void handleAddToCart(Product product, int quantity, Button btn) {
        btn.setDisable(true);
        btn.setText("Adding ...");
        CartService.addCartItem(product.getId(), quantity, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                btn.setDisable(false);
                btn.setText("Added!");
                Platform.runLater(() -> executeAfterDelay(() -> btn.setText("Add to Cart"), 1));
            }

            @Override
            public void onFailure(String errorMessage) {
                btn.setDisable(false);
                btn.setText("Add to Cart");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to add to cart");
                alert.setContentText("Error message: " + errorMessage);
                alert.showAndWait();
            }
        });
    }

    private void showLoading(boolean show) {
        productsContainer.setVisible(!show);
        loadingLabel.setVisible(show);
        loadingLabel.setManaged(show);
    }

    public void handleClearFilter(ActionEvent actionEvent) {
        searchField.clear();
        categoryFilter.getSelectionModel().selectFirst();
        applyFilters();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        cache.remove("products");
        showLoading(true);
        loadProducts();
    }
}
