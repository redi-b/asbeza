package org.asbeza.frontend.controllers.delivery;

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
        navigation.navigateTo("delivery/dashboard.fxml", "Admin - Dashboard", null);
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
