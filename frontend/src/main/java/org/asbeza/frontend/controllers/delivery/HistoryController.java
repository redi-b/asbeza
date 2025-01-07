package org.asbeza.frontend.controllers.delivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.OrderService;
import org.asbeza.frontend.responses.OrderResponse;
import org.asbeza.frontend.types.Order;
import org.asbeza.frontend.types.OrderStatus;
import org.asbeza.frontend.utils.SessionManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController extends CommonController {
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
        OrderService.getOrdersByDeliveryPersonnel(session.getUserId(), new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    OrderResponse orderResponse = objectMapper.readValue(response, OrderResponse.class);
                    updateOrdersList(
                            orderResponse.getData().stream().filter(
                                    order -> (
                                            order.getStatus().equals(OrderStatus.DELIVERED) ||
                                            order.getStatus().equals(OrderStatus.CANCELLED)
                                    )
                            ).toList()
                    );
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
            showEmptyMessage("You have no orders delivered yet :(", orderListContainer);
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

        HBox dateContainer = new HBox(10);

        Label orderCreatedAt = new Label(
                "Created on: " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a"))
        );
        orderCreatedAt.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        String deliveryStatus = order.getStatus().name().toLowerCase();
        String formattedStatus = deliveryStatus.substring(0, 1).toUpperCase() + deliveryStatus.substring(1);
        Label orderUpdatedAt = new Label(
                formattedStatus + " On: " +
                order.getUpdatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a"))
        );
        orderUpdatedAt.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        dateContainer.getChildren().addAll(orderCreatedAt, spacer, orderUpdatedAt);

        Label statusLabel = new Label("Status: " + order.getStatus());
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // Add order details to the VBox
        details.getChildren().addAll(orderNumberLabel, totalPriceLabel, statusLabel, dateContainer);

        // Add the details and button to the card
        HBox.setHgrow(details, Priority.ALWAYS);  // Allow the details section to expand
        card.getChildren().addAll(details);

        return card;
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
