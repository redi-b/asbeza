package org.asbeza.frontend.controllers.admin;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.asbeza.frontend.NavigationHandler;
import org.asbeza.frontend.services.LogoutService;
import org.asbeza.frontend.utils.SessionManager;

public class CommonController extends ErrorController {
    @FXML
    public Label loadingLabel;

    final SessionManager session = SessionManager.getInstance();
    final NavigationHandler navigation = NavigationHandler.getInstance();

    @FXML
    public void goToDashboard() {
        navigation.navigateTo("admin/dashboard.fxml", "Admin - Dashboard", null);
    }

    @FXML
    public void goToAddProduct() {
        navigation.navigateTo("admin/add-product.fxml", "Asbeza - Admin - Add Product", null);
    }

    @FXML
    public void goToManageProducts() {
        navigation.navigateTo("admin/manage-products.fxml", "Asbeza - Admin - Manage Inventory", null);
    }

    @FXML
    public void goToManageUsers() {
        navigation.navigateTo("admin/manage-users.fxml", "Asbeza - Admin - View Users", null);
    }

    @FXML
    public void goToManageOrders() {
        navigation.navigateTo("admin/manage-orders.fxml", "Admin - Dashboard", null);
    }

    @FXML
    public void handleLogout() {
        LogoutService.logOut();
    }

    public void showLoading(boolean show, VBox container) {
        container.setVisible(!show);
        loadingLabel.setVisible(show);
        loadingLabel.setManaged(show);
    }

    public void executeAfterDelay(Runnable callback, int delayInSeconds) {
        // Create a PauseTransition with the specified delay
        PauseTransition pause = new PauseTransition(Duration.seconds(delayInSeconds));

        // Set the event handler to call the callback when the pause is over
        pause.setOnFinished(event -> {
            callback.run(); // Execute the callback
        });

        // Start the pause transition
        pause.play();
    }

    public void showEmptyMessage(String message, VBox container) {
        Label emptyCartLabel = new Label(message);
        emptyCartLabel.setStyle("-fx-font-size: 18px;");
        container.getChildren().add(emptyCartLabel);
        emptyCartLabel.setAlignment(Pos.CENTER);
    }
}
