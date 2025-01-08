package org.asbeza.frontend.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.asbeza.frontend.responses.OrderDetailsResponse;
import org.asbeza.frontend.responses.SingleProductResponse;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.OrderService;
import org.asbeza.frontend.types.*;

import java.util.Arrays;

public class EditOrderController extends CommonController {

    public Label lblUserName;
    public VBox orderDetailsContainer;
    public ComboBox<String> cmbStatus;
    public Button btnSaveOrder;
    public Button btnDeleteOrder;

    private Long orderId;

    public void initialize() {
        lblUserName.setText(session.getUserName());

        // Retrieve the orderId from userData (it can be null, so we check before using it)
        Platform.runLater(() -> {
            Object userData = navigation.getStage().getUserData();

            if (userData instanceof Long) {
                orderId = (Long) userData;
                loadOrderDetails(orderId);  // Load order details using the orderId
            } else {
                setErrorMessage("No user data provided or invalid data");
                showError(true);
            }

            Arrays.stream(OrderStatus.values()).forEach((status) -> {
                cmbStatus.getItems().add(status.name());
            });
        });
    }

    private void loadOrderDetails(Long orderId) {
        if (orderId == null) {
            setErrorMessage("No user data provided or invalid data");
            showError(true);
            return;
        }

        // Fetch order details using the backend service
        OrderService.getOrderById(orderId, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                try {
                    OrderDetailsResponse orderResponse = objectMapper.readValue(response, OrderDetailsResponse.class);
                    fillDetails(orderResponse.getData());
                } catch (JsonProcessingException e) {
                    setErrorMessage(e.getMessage());
                    showError(true);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                setErrorMessage(errorMessage);
                showError(true);
            }
        });
    }

    private void fillDetails(Order order) {
        // Populate the fields with the order data
        cmbStatus.getSelectionModel().select(order.getStatus().name());
        createOrderItemsTable(order);
    }

    @FXML
    private void handleSaveChanges(ActionEvent actionEvent) {
        saveActionStart();

        // Get the updated delivery status from the text field
        OrderStatus updatedStatus = OrderStatus.valueOf(cmbStatus.getValue());

        // Update the order status using the backend service
        OrderService.updateOrderStatus(orderId, updatedStatus.toString(), new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                showError(false);
                saveActionEnd();

                // Handle success by showing an alert with the success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(btnSaveOrder.getScene().getWindow());
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Order status updated!");

                // Show the alert
                alert.showAndWait();
            }

            @Override
            public void onFailure(String errorMessage) {
                saveActionEnd();
                setErrorMessage(errorMessage);
                showError(true);
            }
        });
    }

    private void createOrderItemsTable(Order order) {
        // Create the TableView
        TableView<OrderItem> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Auto-resize columns
        table.getStyleClass().add("modern-table");

        // Create columns
        TableColumn<OrderItem, String> productNameColumn = new TableColumn<>("Product Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<OrderItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<OrderItem, Double> priceColumn = new TableColumn<>("Price (Birr)");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));

        // Add columns to the table
        //noinspection unchecked
        table.getColumns().addAll(productNameColumn, quantityColumn, priceColumn);

        // Populate the table with data from the order
        table.setItems(FXCollections.observableArrayList(order.getItems()));
        table.setPrefHeight(200);

        // Add the table to the container
        orderDetailsContainer.getChildren().add(table);

        // Display total price below the table
        HBox totalPriceBox = new HBox();
        totalPriceBox.setAlignment(Pos.CENTER_RIGHT); // Align to the right
        Label totalPriceLabel = new Label("Total Price: " + String.format("%.2f Birr", order.getTotalPrice()));
        totalPriceLabel.getStyleClass().add("order-info-text");
        totalPriceBox.getChildren().add(totalPriceLabel);
        orderDetailsContainer.getChildren().add(totalPriceBox);
    }

    private void saveActionStart() {
        btnSaveOrder.setDisable(true);
        btnSaveOrder.setText("Saving changes ...");
    }

    private void saveActionEnd() {
        btnSaveOrder.setDisable(false);
        btnSaveOrder.setText("Save Changes");
    }

    @FXML
    private void handleDeleteOrder(ActionEvent actionEvent) {
        if (orderId == null) {
            return;
        }

        deleteActionStart();
        OrderService.cancelOrder(orderId, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                deleteActionEnd();
                // Show success alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(btnDeleteOrder.getScene().getWindow());
                alert.setTitle("Order Deleted Successfully");
                alert.setHeaderText("Order has been cancelled.");
                alert.getDialogPane().getStyleClass().add("alert-information");
                alert.setContentText(
                        "You have cancelled order #" + orderId
                );
                alert.showAndWait();

                goToManageOrders();
            }

            @Override
            public void onFailure(String errorMessage) {
                deleteActionEnd();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(btnDeleteOrder.getScene().getWindow());
                alert.setTitle("Error");
                alert.setHeaderText("Failed to delete order");
                alert.setContentText("Error message: " + errorMessage);
                alert.showAndWait();
            }
        });
    }

    private void deleteActionStart() {
        btnDeleteOrder.setDisable(true);
        btnDeleteOrder.setText("Deleting order ...");
    }

    private void deleteActionEnd() {
        btnDeleteOrder.setDisable(false);
        btnDeleteOrder.setText("Delete Order");
    }
}
