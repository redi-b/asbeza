package org.asbeza.frontend.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.asbeza.frontend.forms.Validator;
import org.asbeza.frontend.responses.SingleProductResponse;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.ProductService;
import org.asbeza.frontend.types.Product;
import org.asbeza.frontend.utils.JsonUtils;

import java.math.BigDecimal;

public class EditProductController extends CommonController {
    public Label lblUserName;
    public Button btnSaveProduct;
    @FXML private TextField txtProductName;
    @FXML private TextField txtPrice;
    @FXML public TextField txtPricePer;
    @FXML private TextField txtCategory;
    @FXML private TextField txtStockQuantity;
    @FXML private TextField txtDiscountPercentage;
    @FXML private TextArea txtDescription;

    private Long productId;

    public void initialize() {
        lblUserName.setText(session.getUserName());

        // Retrieve the productId from userData (it can be null, so we check before using it)
        Platform.runLater(() -> {
            Object userData = navigation.getStage().getUserData();

            if (userData instanceof Long) {
                productId = (Long) userData;
                loadProductDetails(productId);  // Load product details using the productId
            } else {
                setErrorMessage("No user data provided or invalid data");
                showError(true);
            }
        });
    }

    private void loadProductDetails(Long productId) {
        if (productId == null) {
            setErrorMessage("No user data provided or invalid data");
            showError(true);
            return;
        }

        ProductService.getProductById(productId, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    SingleProductResponse productResponse = objectMapper.readValue(response, SingleProductResponse.class);
                    fillDetails(productResponse.getData());
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

    private void fillDetails(Product product) {
        // Populate the fields with the product data
        txtProductName.setText(product.getName());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtPricePer.setText(product.getPricePer());
        txtCategory.setText(product.getCategory());
        txtStockQuantity.setText(String.valueOf(product.getStockQuantity()));
        txtDiscountPercentage.setText(String.valueOf(product.getDiscountPercentage()));
        txtDescription.setText(product.getDescription());
    }

    @FXML
    private void handleSaveProduct() {
        saveActionStart();

        // Collect data from the input fields
        String productName = txtProductName.getText(),
                priceText = txtPrice.getText(),
                category = txtCategory.getText(),
                stockQuantityText = txtStockQuantity.getText(),
                discountPercentageText = txtDiscountPercentage.getText(),
                description = txtDescription.getText();

        String error = Validator.validateAtLeastOneFieldRequired(
                productName, priceText, category, stockQuantityText, discountPercentageText, description
        );

        if (error != null) {
            setErrorMessage(error);
            showError(true);
            return;
        }

        int stockQuantity;
        BigDecimal price, discountPercentage;

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


        String productJson = JsonUtils.createProductJson(
                productName, price, txtPricePer.getText(), category, stockQuantity, discountPercentage, description
        );

        ProductService.updateProduct(productId, productJson, null, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                clearFields();
                showError(false);
                saveActionEnd();

                // Handle success by showing an alert with the success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Product updated successfully!");

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

    private void saveActionStart() {
        btnSaveProduct.setDisable(true);
        btnSaveProduct.setText("Saving Product ...");
    }

    private void saveActionEnd() {
        btnSaveProduct.setDisable(false);
        btnSaveProduct.setText("Save Product");
    }

    private void clearFields() {
        loadProductDetails(productId);
    }
}
