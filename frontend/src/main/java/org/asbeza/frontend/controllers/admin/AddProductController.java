package org.asbeza.frontend.controllers.admin;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.asbeza.frontend.forms.Validator;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.ProductService;
import org.asbeza.frontend.utils.JsonUtils;

import java.io.File;
import java.math.BigDecimal;

public class AddProductController extends CommonController {
    public Label lblUserName;

    // Form inputs
    public TextField txtProductName;
    public TextField txtPrice;
    public TextField txtPricePer;
    public TextField txtCategory;
    public TextField txtStockQuantity;
    public TextArea txtDescription;
    public Button btnAddProduct;
    public Label lblFileName;
    public Button btnChooseFile;

    @FXML
    private TextField txtDiscountPercentage;
    private String imagePath;

    public void initialize() {
        lblUserName.setText(session.getUserName());
        // Restrict Stock Quantity to numbers only
        txtStockQuantity.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d*")) ? change : null));

        // Restrict Discount Percentage to numbers only
        txtDiscountPercentage.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d*")) ? change : null));

        Platform.runLater(() -> {
            txtProductName.requestFocus();
        });

        btnAddProduct.setOnAction(this::handleAddProduct);

    }

    public void handleAddProduct(ActionEvent actionEvent) {
        addActionStart();

        // Collect data from the input fields
        String productName = txtProductName.getText(),
                priceText = txtPrice.getText(),
                category = txtCategory.getText(),
                stockQuantityText = txtStockQuantity.getText(),
                discountPercentageText = txtDiscountPercentage.getText(),
                description = txtDescription.getText();

        int stockQuantity;
        BigDecimal price, discountPercentage;

        // Validate required fields
        String nameError = Validator.validateRequiredField(productName, "Product name");
        String priceError = Validator.validateRequiredField(priceText, "Price");
        String stockQuantityError = Validator.validateRequiredField(stockQuantityText, "Stock Quantity");

        if (nameError != null) {
            setErrorMessage(nameError);
            showError(true);
            addActionEnd();
            return;
        }

        if (priceError != null) {
            setErrorMessage(priceError);
            showError(true);
            addActionEnd();
            return;
        }

        if (stockQuantityError != null) {
            setErrorMessage(stockQuantityError);
            showError(true);
            addActionEnd();
            return;
        }

        // Validate selected file
        if (lblFileName.getText().equals("No file chosen")) {
            setErrorMessage("Please choose an image file for the product.");
            showError(true);
            addActionEnd();
            return;
        }

        // Default values for category and description if blank
        if (category.isBlank()) category = "Uncategorized";
        if (description.isBlank()) description = "No description";

        // Parse discountPercentage, default to 0 if empty
        if (discountPercentageText.isBlank()) {
            discountPercentage = BigDecimal.ZERO;
        } else {
            discountPercentage = BigDecimal.valueOf(Double.parseDouble(discountPercentageText));
        }

        // Parse price and stock quantity
        price = BigDecimal.valueOf(Double.parseDouble(priceText));
        stockQuantity = Integer.parseInt(stockQuantityText);

        // Create product JSON string
        File imageFile = new File(imagePath);
        String productJson = JsonUtils.createProductJson(
                productName, price, txtPricePer.getText(), category, stockQuantity, discountPercentage, description);

        // Use ProductService to create the product
        ProductService.createProduct(productJson, imageFile, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                clearFields();
                showError(false);
                addActionEnd();

                // Handle success by showing an alert with the success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(btnAddProduct.getScene().getWindow());
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Product added successfully!");

                // Show the alert
                alert.showAndWait();
            }

            @Override
            public void onFailure(String errorMessage) {
                setErrorMessage(errorMessage);
                showError(true);

                addActionEnd();
            }
        });
    }

    @FXML
    public void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(btnChooseFile.getScene().getWindow());
        if (selectedFile != null) {
            lblFileName.setText(selectedFile.getName());
            imagePath = selectedFile.getAbsolutePath();
        } else {
            lblFileName.setText("No file chosen");
        }
    }
    
    private void clearFields() {
        txtProductName.clear();
        txtPrice.clear();
        txtDiscountPercentage.clear();
        txtCategory.clear();
        txtDescription.clear();
        txtStockQuantity.clear();
        txtDiscountPercentage.clear();
        lblFileName.setText("No file chosen");
    }


    private void addActionStart() {
        btnAddProduct.setDisable(true);
        btnAddProduct.setText("Adding Product ...");
    }

    private void addActionEnd() {
        btnAddProduct.setDisable(false);
        btnAddProduct.setText("Add Product");
    }
}
