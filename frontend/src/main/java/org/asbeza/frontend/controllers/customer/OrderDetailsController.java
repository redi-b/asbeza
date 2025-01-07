package org.asbeza.frontend.controllers.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

import org.asbeza.frontend.responses.OrderDetailsResponse;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.OrderService;
import org.asbeza.frontend.types.Order;
import org.asbeza.frontend.types.OrderItem;
import org.asbeza.frontend.utils.SessionManager;

import java.time.format.DateTimeFormatter;

public class OrderDetailsController extends CommonController {
    @FXML
    public Label lblUserName;
    @FXML
    public Label orderNumberLabel;
    @FXML
    public Label orderDateLabel;
    @FXML
    public Label orderTimeLabel;
    @FXML
    public Label orderStatusLabel;
    @FXML
    public VBox orderDetailsContainer;
    @FXML
    public Label phoneNumberLabel;
    @FXML
    public Label locationLabel;

    private final SessionManager session = SessionManager.getInstance();

    @FXML
    public void initialize() {
        lblUserName.setText(session.getUserName());
        Platform.runLater(() -> {
            Long orderId = (Long) navigation.getUserData(); // Cast to Long for safety
            if (orderId != null) {
                fetchOrderDetails(orderId);
            } else {
                setErrorMessage("No order ID found in navigation data.");
                showError(true);
            }
        });
    }

    private void fetchOrderDetails(Long orderId) {
        OrderService.getOrderById(orderId, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    OrderDetailsResponse order = objectMapper.readValue(response, OrderDetailsResponse.class);
                    updateOrderDetails(order.getData());
                } catch (Exception e) {
                    setErrorMessage("\"Could not parse order details: \" + e.getMessage()");
                    showError(true);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                setErrorMessage("\"Could not fetch order details: \" + e.getMessage()");
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
        table.setPrefHeight(100);

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


    private void updateOrderDetails(Order order) {
        orderNumberLabel.setText("Order #" + order.getId());
        orderDateLabel.setText(
                "Date placed: " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        );
        orderTimeLabel.setText(
                    "Time placed: " + order.getCreatedAt().toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm a"))
        );
        orderStatusLabel.setText("Status: " + order.getStatus());
        locationLabel.setText("Address: " + order.getAddress());
        phoneNumberLabel.setText("Phone Number: " + order.getPhoneNumber());

        // Clear placeholder text and prepare orderDetailsContainer
        Platform.runLater(() -> {
            // Clear the container
            orderDetailsContainer.getChildren().clear();

            createOrderItemsTable(order);
        });
    }

    public void handleRefresh(ActionEvent actionEvent) {
        fetchOrderDetails((Long) navigation.getUserData());
    }
}