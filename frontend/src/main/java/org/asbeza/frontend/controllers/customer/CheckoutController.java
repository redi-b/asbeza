package org.asbeza.frontend.controllers.customer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.asbeza.frontend.forms.Validator;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.OrderService;
import org.asbeza.frontend.types.Cart;
import org.asbeza.frontend.types.CartItem;

import java.util.concurrent.TimeUnit;

public class CheckoutController extends CommonController {
    public TextField shippingAddressField;
    public TextField shippingPhoneField;
    public Label lblUserName;
    public VBox cartDetailsContainer;
    public Button confirmOrderButton;

    public void initialize() {
        lblUserName.setText(session.getUserName());
        Platform.runLater(() -> {
            // Clear the container
            cartDetailsContainer.getChildren().clear();

            createCartItemsTable((Cart) navigation.getUserData());
        });
    }

    private void createCartItemsTable(Cart cart) {
        // Create the TableView
        TableView<CartItem> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Auto-resize columns
        table.getStyleClass().add("modern-table");

        // Create columns
        TableColumn<CartItem, String> productNameColumn = new TableColumn<>("Product Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<CartItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<CartItem, Double> priceColumn = new TableColumn<>("Price (Birr)");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("productPrice"));

        // Add columns to the table
        //noinspection unchecked
        table.getColumns().addAll(productNameColumn, quantityColumn, priceColumn);

        // Populate the table with data from the order
        table.setItems(FXCollections.observableArrayList(cart.getItems()));
        table.setPrefHeight(700);

        // Add the table to the container
        cartDetailsContainer.getChildren().add(table);

        // Display total price below the table
        HBox totalPriceBox = new HBox();
        totalPriceBox.setAlignment(Pos.CENTER_RIGHT); // Align to the right
        Label totalPriceLabel = new Label("Total Price: " + String.format("%.2f Birr", cart.getTotalPrice()));
        totalPriceLabel.getStyleClass().add("order-info-text");
        totalPriceBox.getChildren().add(totalPriceLabel);
        cartDetailsContainer.getChildren().add(totalPriceBox);
    }

    public void handleConfirmOrder(ActionEvent actionEvent) {
        confirmOrderStart();

        String address = shippingAddressField.getText();
        String phoneNumber = shippingPhoneField.getText();

        String addressError = Validator.validateRequiredField(address, "Shipping Address");
        String phoneNumberError = Validator.validatePhoneNumber(phoneNumber);

        if (addressError != null) {
            setErrorMessage(addressError);
            showError(true);
            confirmOrderEnd();
            return;
        }

        if (phoneNumberError != null) {
            setErrorMessage(phoneNumberError);
            showError(true);
            confirmOrderEnd();
            return;
        }

        // Call the OrderService to place the order
        OrderService.createOrder(address, phoneNumber, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    TimeUnit.MILLISECONDS.sleep(800); // Simulate processing delay
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }

                confirmOrderEnd();

                // Show success alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Placed Successfully");
                alert.setHeaderText("Your order has been placed successfully.");
                alert.getDialogPane().getStyleClass().add("alert-information");
                alert.setContentText(
                        "Please check your email for payment instructions. " +
                        "Complete the payment within 2 hours to avoid order cancellation. "
                );
                alert.showAndWait();

                goToBrowse();
            }

            @Override
            public void onFailure(String errorMessage) {
                confirmOrderEnd();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to place order");
                alert.setContentText("Error message: " + errorMessage);
                alert.showAndWait();
            }
        });
    }

    private void confirmOrderStart() {
        // Disable the button and update its text
        confirmOrderButton.setDisable(true);
        confirmOrderButton.setText("Placing Order...");
    }

    private void confirmOrderEnd() {
        // Re-enable the button and reset its text
        confirmOrderButton.setDisable(false);
        confirmOrderButton.setText("Confirm Order");
    }
}
