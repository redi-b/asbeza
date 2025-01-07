package org.asbeza.frontend.controllers.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.OrderService;
import org.asbeza.frontend.responses.OrderResponse;
import org.asbeza.frontend.types.Order;
import org.asbeza.frontend.utils.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdersController extends CommonController {
    @FXML
    public VBox orderListContainer;
    @FXML
    public Label lblUserName;
    @FXML
    public Label loadingLabel;
    @FXML
    public Label errorLabel;

    private final SessionManager session = SessionManager.getInstance();

    @FXML
    public void initialize() {
        lblUserName.setText(session.getUserName());
        showError(false);
        showLoading(true);

        loadOrders();
    }

    private void loadOrders() {
        OrderService.getOrdersForUser(new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    OrderResponse orders = objectMapper.readValue(response, OrderResponse.class);
                    updateOrdersList(orders.getData());
                    showLoading(false);
                } catch (Exception e) {
                    showLoading(false);
                    showError(true);
                    setErrorMessage("Failed to load orders: " + e.getMessage());
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

    private void updateOrdersList(List<Order> orders) {
        orderListContainer.getChildren().clear();

        if (orders.isEmpty()) {
            showEmptyMessage("You have no orders :(", orderListContainer);
        }

        for (Order order : orders) {
            orderListContainer.getChildren().add(createOrderCard(order));
        }
    }

    private HBox createOrderCard(Order order) {
        HBox card = new HBox(15);
        card.setStyle("-fx-padding: 10; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefHeight(120);

        // Order Details
        VBox details = new VBox(5);

        Label orderNumberLabel = new Label("Order #" + order.getId());
        orderNumberLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label totalPriceLabel = new Label("Total: " + String.format("%.2f", order.getTotalPrice()) + " Birr");
        totalPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

        Label dateLabel = new Label(
                "Placed on: " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy | hh:mm a"))
        );
        dateLabel.setStyle("-fx-font-size: 12px;");

        Label statusLabel = new Label("Status: " + order.getStatus());
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // Add order details to the VBox
        details.getChildren().addAll(orderNumberLabel, totalPriceLabel, dateLabel, statusLabel);

        // Button to navigate to order details
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.getStyleClass().add("navbar-btn");
        viewDetailsButton.setOnAction(e -> navigateToOrderDetails(order.getId()));

        HBox buttonContainer = new HBox();
        buttonContainer.setStyle("-fx-alignment: center-right;");
        buttonContainer.getChildren().add(viewDetailsButton);

        // Add the details and button to the card
        HBox.setHgrow(details, Priority.ALWAYS);  // Allow the details section to expand
        card.getChildren().addAll(details, buttonContainer);

        return card;
    }

    private void navigateToOrderDetails(Long orderId) {
        // Handle navigation to the order details page
        navigation.navigateTo("customer/order-details.fxml", "Asbeza - Order Details", orderId);
    }

    private void showLoading(boolean show) {
        orderListContainer.setVisible(!show);
        loadingLabel.setVisible(show);
        loadingLabel.setManaged(show);
    }

    public void handleRefresh(ActionEvent actionEvent) {
        showLoading(true);
        loadOrders();
    }
}
