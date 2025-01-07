package org.asbeza.frontend.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import org.asbeza.frontend.responses.ProductResponse;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.ProductService;
import org.asbeza.frontend.types.Product;
import org.asbeza.frontend.utils.ImageUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ManageProductsController extends CommonController {
    public VBox productsContainer;
    public Label lblUserName;
    public TextField searchField;
    public ComboBox<String> categoryFilter;

    private final ObservableList<Product> allProducts = FXCollections.observableArrayList();
    private final ObservableList<Product> filteredProducts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        lblUserName.setText(session.getUserName());
        Platform.runLater(this::fetchAndUpdateProducts);

        // Add event listeners for filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void fetchAndUpdateProducts() {
        showError(false);
        showLoading(true, productsContainer);

        ProductService.getAllProducts(new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    ProductResponse productResponse = objectMapper.readValue(response, ProductResponse.class);

                    // Update product lists
                    allProducts.setAll(productResponse.getData());
                    filteredProducts.setAll(allProducts);
                    loadCategories(); // Populate category filter
                    updateProductContainer();

                    showLoading(false, productsContainer);
                } catch (JsonProcessingException e) {
                    showLoading(false, productsContainer);
                    setErrorMessage(e.getMessage());
                    showError(true);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                showLoading(false, productsContainer);
                setErrorMessage(errorMessage);
                showError(true);
            }
        });
    }

    private void loadCategories() {
        Set<String> categories = new HashSet<>();
        categories.add("All");
        categories.addAll(allProducts.stream().map(Product::getCategory).collect(Collectors.toSet()));

        categoryFilter.getItems().clear();
        categoryFilter.getItems().addAll(categories);
        categoryFilter.getSelectionModel().selectFirst(); // Default to "All"
    }

    private void applyFilters() {
        String searchQuery = searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryFilter.getValue();

        filteredProducts.setAll(allProducts.stream()
                .filter(product -> (searchQuery.isEmpty() || product.getName().toLowerCase().contains(searchQuery)))
                .filter(product -> ("All".equals(selectedCategory) || product.getCategory().equalsIgnoreCase(selectedCategory)))
                .collect(Collectors.toList()));

        updateProductContainer();
    }

    private void updateProductContainer() {
        productsContainer.getChildren().clear();

        if (filteredProducts.isEmpty()) {
            showEmptyMessage("No products found.", productsContainer);
        } else {
            for (Product product : filteredProducts) {
                productsContainer.getChildren().add(createProductCard(product));
            }
        }
    }

    private HBox createProductCard(Product product) {
        HBox card = new HBox(15);
        card.setStyle("-fx-padding: 10; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefHeight(140);

        ImageView productImageView = getImageView(card, product.getImage());

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

        Label categoryLabel = new Label("Category: " + product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Label stockLabel = new Label("Stock: " + product.getStockQuantity());
        stockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Hyperlink editLink = new Hyperlink("Edit");
        editLink.setStyle("-fx-text-fill: #007bff;");
        editLink.setOnAction(event -> navigateToEditPage(product.getId()));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("button-red");
        deleteButton.setOnAction(event -> deleteProduct(product.getId(), card));

        details.getChildren().addAll(nameLabel, priceContainer, categoryLabel, stockLabel, editLink);

        VBox actions = new VBox(10);
        actions.setStyle("-fx-alignment: CENTER;");
        actions.getChildren().add(deleteButton);

        HBox.setHgrow(details, Priority.ALWAYS);
        card.getChildren().addAll(productImageView, details, actions);

        return card;
    }

    private static ImageView getImageView(HBox card, String imageStr) {
        ImageView productImageView = new ImageView();
        productImageView.setPreserveRatio(false); // Allow image to stretch
        productImageView.setFitWidth(card.getPrefHeight()); // Fixed width
        productImageView.setFitHeight(card.getPrefHeight()); // Match the card height

        // Add rounded corners to the image
        Rectangle clip = new Rectangle(card.getPrefHeight(), card.getPrefHeight()); // Same dimensions as the image
        clip.setArcWidth(15); // Rounded corners width
        clip.setArcHeight(15); // Rounded corners height
        productImageView.setClip(clip);

        // Load product image
        Image image = ImageUtil.createImage(imageStr);
        productImageView.setImage(image);
        return productImageView;
    }

    private void deleteProduct(Long productId, HBox card) {
        ProductService.deleteProduct(productId, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                productsContainer.getChildren().remove(card);
            }

            @Override
            public void onFailure(String errorMessage) {
                setErrorMessage("Failed to delete product: " + errorMessage);
            }
        });
    }


    // Method to navigate to the "Modify/Edit Product" page
    private void navigateToEditPage(Long productId) {
        navigation.navigateTo("admin/edit-product.fxml", "Asbeza - Admin - Edit Product", productId);
    }

    public void handleClearFilter(ActionEvent event) {
        searchField.clear();
        categoryFilter.getSelectionModel().selectFirst();
        applyFilters();
    }
}
