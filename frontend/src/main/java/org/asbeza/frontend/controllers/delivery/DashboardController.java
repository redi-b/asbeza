package org.asbeza.frontend.controllers.delivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.asbeza.frontend.responses.OrderResponse;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.OrderService;
import org.asbeza.frontend.types.Order;
import org.asbeza.frontend.types.OrderItem;
import org.asbeza.frontend.types.OrderStatus;

import java.util.List;

public class DashboardController extends CommonController {


    public Label lblUserName;
    public VBox ordersContainer;

    public void initialize () {
        lblUserName.setText(session.getUserName());

        Platform.runLater(this::fetchAndUpdateOrders);
    }

    private void loadOrders(List<Order> orders) {
        ordersContainer.getChildren().clear();

        if (orders.isEmpty()) {
            showEmptyMessage("No orders to deliver at this time.", ordersContainer);
            return;
        }

        // Add each order as a card to the VBox
        for (Order order : orders) {
            ordersContainer.getChildren().add(createOrderCard(order));
        }
    }

    private void fetchAndUpdateOrders() {
        showError(false);
        showLoading(true, ordersContainer);

        OrderService.getOrdersByDeliveryPersonnel(session.getUserId(), new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                try {
                    OrderResponse orderResponse = objectMapper.readValue(response, OrderResponse.class);
                    loadOrders(
                            orderResponse.getData().stream().filter(
                                    order -> (
                                            !order.getStatus().equals(OrderStatus.DELIVERED) &&
                                            !order.getStatus().equals(OrderStatus.CANCELLED)
                                            )
                            ).toList()
                    );
                    showLoading(false, ordersContainer);
                } catch (JsonProcessingException e) {
                    showLoading(false, ordersContainer);
                    setErrorMessage(e.getMessage());
                    showError(true);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                showLoading(false, ordersContainer);
                setErrorMessage(errorMessage);
                showError(true);
            }
        });
    }


    // Method to create an order card
    private HBox createOrderCard(Order order) {
        HBox card = new HBox(15);
        card.setStyle("""
        -fx-padding: 15; 
        -fx-border-color: #e0e0e0; 
        -fx-border-width: 1; 
        -fx-border-radius: 8; 
        -fx-background-color: #ffffff; 
        -fx-background-radius: 8;
        -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 5);
    """);
        card.setPrefHeight(150);

        // Order Details Section
        VBox details = new VBox(10);

        // Order ID as a hyperlink
        Hyperlink orderLink = new Hyperlink("Order #" + order.getId());
        orderLink.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #007bff;");
        orderLink.setOnAction(event -> navigateToEditPage(order.getId()));

        // Order Status
        Label orderStatus = new Label("Status: " + order.getStatus());
        orderStatus.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #444444;");

        // Order Total
        Label orderTotal = new Label(String.format("Total: %.2f Birr", order.getTotalPrice()));
        orderTotal.setStyle("-fx-font-size: 14px; -fx-text-fill: #28a745;");

        // Order Date
        Label orderDate = new Label("Order Date: " + order.getCreatedAt().toLocalDate());
        orderDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");

        // Address and Phone Number
        Label orderAddress = new Label("Delivery Address: " + order.getAddress());
        orderAddress.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        Label phoneNumber = new Label("Phone: " + order.getPhoneNumber());
        phoneNumber.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        // Add all details to the VBox
        details.getChildren().addAll(orderLink, orderStatus, orderTotal, orderDate, orderAddress, phoneNumber);

        // Items Section
        VBox itemsBox = new VBox(5);
        Label itemsHeader = new Label("Items:");
        itemsHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #444444;");
        itemsBox.getChildren().add(itemsHeader);

        // Add each item to the itemsBox
        for (OrderItem item : order.getItems()) {
            Label itemLabel = new Label("- " + item.getProductName() + " x" + item.getQuantity());
            itemLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
            itemsBox.getChildren().add(itemLabel);
        }

        // Layout for details and items
        HBox.setHgrow(details, Priority.ALWAYS);
        card.getChildren().addAll(details, itemsBox);

        return card;
    }


    // Method to navigate to the "Edit Order" page
    private void navigateToEditPage(Long orderId) {
        navigation.navigateTo("delivery/edit-order.fxml", "Asbeza - Admin - Edit Order", orderId);
    }

    public void goToDeliveryHistory() {
        navigation.navigateTo("delivery/history.fxml", "Asbeza - Delivery History", null);
    }

    public void handleRefresh() {
        fetchAndUpdateOrders();
    }
}
