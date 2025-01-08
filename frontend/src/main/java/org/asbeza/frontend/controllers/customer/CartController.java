package org.asbeza.frontend.controllers.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.CartService;
import org.asbeza.frontend.types.Cart;
import org.asbeza.frontend.types.CartItem;
import org.asbeza.frontend.responses.CartResponse;
import org.asbeza.frontend.utils.ImageUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CartController extends CommonController {
    @FXML
    public VBox cartItemsContainer;
    @FXML
    public Label totalLabel;
    @FXML
    public Label lblUserName;
    @FXML
    public Label loadingLabel;
    @FXML
    public Label errorLabel;
    @FXML
    public Button checkoutBtn;

    private final Map<String, Image> imageCache = new ConcurrentHashMap<>();
    private double totalPrice = 0.0;

    @FXML
    public void initialize() {
        checkoutBtn.setDisable(true);
        showError(false);
        showLoading(true);
        lblUserName.setText(session.getUserName());
        Platform.runLater(this::loadCartItems);
    }

    private void loadCartItems() {
        CartService.getUserCart(new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    CartResponse cartResponse = objectMapper.readValue(response, CartResponse.class);

                    updateCartItemsContainer(cartResponse.getItems());
                    if (!cartResponse.getItems().isEmpty()) checkoutBtn.setDisable(false);
                    checkoutBtn.setOnAction((event) -> handleCheckout(cartResponse));
                    showError(false);
                    showLoading(false);
                } catch (Exception e) {
                    showLoading(false);
                    showError(true);
                    setErrorMessage("Failed to load cart: " + e.getMessage());
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

    private void updateCartItemsContainer(List<CartItem> cartItems) {
        cartItemsContainer.getChildren().clear();
        totalPrice = 0.0;

        if (cartItems.isEmpty()) {
            showEmptyMessage("Cart is empty :(", cartItemsContainer);
            return;
        }

        for (CartItem item : cartItems) {
            cartItemsContainer.getChildren().add(createCartItemCard(item));
            totalPrice += item.getProductPrice() * item.getQuantity();
        }

        totalLabel.setText("Total: " + String.format("%.2f", totalPrice) + " Birr");
    }

    private HBox createCartItemCard(CartItem item) {
        HBox card = new HBox(15);
        card.setStyle("-fx-padding: 10; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefHeight(120);

        // Lazy Load Image
        ImageView productImageView = new ImageView();
        productImageView.setFitHeight(100);
        productImageView.setFitWidth(100);
        loadProductImageAsync(item.getImage(), productImageView);
        applyRoundedCorners(productImageView);

        // Product Details
        VBox details = new VBox(5);

        Label nameLabel = new Label(item.getProductName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label priceLabel = new Label("Price: " + String.format("%.2f", item.getProductPrice()) + " Birr");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

        HBox quantityContainer = new HBox(5); // New HBox for quantity editing

        TextField quantityField = new TextField(String.valueOf(item.getQuantity()));
        quantityField.setPrefWidth(30);
        quantityField.setStyle("-fx-padding: 6 8;");
        quantityField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d*")) ? change : null)); // Restrict to numbers

        Button decrementButton = new Button("-");
        decrementButton.getStyleClass().add("circular-button");
        decrementButton.setOnAction(e -> handleQuantityChange(item, quantityField, -1));

        Button incrementButton = new Button("+");
        incrementButton.getStyleClass().add("circular-button");
        incrementButton.setOnAction(e -> handleQuantityChange(item, quantityField, 1));

        quantityContainer.getChildren().addAll(decrementButton, quantityField, incrementButton);

        details.getChildren().addAll(nameLabel, priceLabel, quantityContainer);

        Button removeItemButton = new Button("Remove");
        removeItemButton.getStyleClass().add("button-red");
        removeItemButton.setOnAction(e -> handleDeleteItem(item, card));

        HBox buttonContainer = new HBox();
        buttonContainer.setStyle("-fx-alignment: center-right;");
        buttonContainer.getChildren().addAll(removeItemButton);

        HBox.setHgrow(details, Priority.ALWAYS);
        card.getChildren().addAll(productImageView, details, buttonContainer);

        return card;
    }

    private void handleQuantityChange(CartItem item, TextField quantityField, int change) {
        int currentQuantity = Integer.parseInt(quantityField.getText());
        int newQuantity = Math.max(1, currentQuantity + change); // Ensure quantity stays positive

        if (newQuantity != currentQuantity) {
            quantityField.setText(String.valueOf(newQuantity));
            CartService.updateCartItemQuantity(item.getProductId(), newQuantity, new ApiRequestService.ApiRequestCallback() {
                @Override
                public void onSuccess(String response) {
                    // Update cart total price (consider implementing in a separate method)
                    totalPrice -= item.getProductPrice() * currentQuantity;
                    totalPrice += item.getProductPrice() * newQuantity;
                    totalLabel.setText("Total: " + String.format("%.2f", totalPrice) + " Birr");
                    // Invalidate cache
                    cache.remove("cartItems");
                    item.setQuantity(newQuantity); // Update local cart item data
                }

                @Override
                public void onFailure(String errorMessage) {
                    setErrorMessage("Failed to update quantity: " + errorMessage);
                    quantityField.setText(String.valueOf(currentQuantity)); // Revert quantity change
                }
            });
        }
    }

    private void applyRoundedCorners(ImageView imageView) {
        double radius = 10; // Set the corner radius
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(radius);
        clip.setArcHeight(radius);
        imageView.setClip(clip);
    }

    private void loadProductImageAsync(String imageUrl, ImageView imageView) {
        if (imageCache.containsKey(imageUrl)) {
            Platform.runLater(() -> imageView.setImage(imageCache.get(imageUrl)));
        } else {
            new Thread(() -> {
                try {
                    Image image = ImageUtil.createImage(imageUrl);
                    imageCache.put(imageUrl, image);
                    Platform.runLater(() -> imageView.setImage(image));
                } catch (Exception e) {
                    System.err.println("Failed to load image: " + e.getMessage());
                }
            }).start();
        }
    }

    private void handleDeleteItem(CartItem item, HBox card) {
        CartService.removeCartItem(item.getProductId(), new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                cartItemsContainer.getChildren().remove(card);
                totalPrice -= item.getProductPrice() * item.getQuantity();
                totalLabel.setText("Total: " + String.format("%.2f", totalPrice) + " Birr");
                if (cartItemsContainer.getChildren().isEmpty())
                    showEmptyMessage("Cart is empty :(", cartItemsContainer);
            }

            @Override
            public void onFailure(String errorMessage) {
                setErrorMessage("Failed to remove item: " + errorMessage);
            }
        });
    }

    public void handleCheckout(CartResponse cartResponse) {
        Cart cart = new Cart();
        cart.setItems(cartResponse.getItems());
        cart.setTotalPrice(cartResponse.getTotalPrice());
        navigation.navigateTo("customer/checkout.fxml", "Asbeza - Checkout", cart);
    }

    private void showLoading(boolean show) {
        cartItemsContainer.setVisible(!show);
        loadingLabel.setVisible(show);
        loadingLabel.setManaged(show);
    }

    public void handleRefresh(ActionEvent actionEvent) {
        cache.remove("cartItems");
        showError(false);
        showLoading(true);
        loadCartItems();
    }
}
