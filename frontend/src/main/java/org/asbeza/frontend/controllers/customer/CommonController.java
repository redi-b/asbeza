package org.asbeza.frontend.controllers.customer;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.asbeza.frontend.NavigationHandler;
import org.asbeza.frontend.controllers.admin.ErrorController;
import org.asbeza.frontend.services.LogoutService;
import org.asbeza.frontend.utils.CacheManager;
import org.asbeza.frontend.utils.SessionManager;

public class CommonController extends ErrorController {

    final SessionManager session = SessionManager.getInstance();
    final NavigationHandler navigation = NavigationHandler.getInstance();
    final CacheManager cache = CacheManager.getInstance();

    @FXML
    protected void goToBrowse() {
        navigation.navigateTo("customer/browse.fxml", "Asbeza - Browse Products", null);
    }

    @FXML
    protected void goToCart() {
        navigation.navigateTo("customer/cart.fxml", "Asbeza - My Cart", null);
    }

    @FXML
    protected void goToOrders() {
        navigation.navigateTo("customer/orders.fxml", "Asbeza - Order History", null);
    }

    @FXML
    protected void handleLogout() {
        LogoutService.logOut();
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
