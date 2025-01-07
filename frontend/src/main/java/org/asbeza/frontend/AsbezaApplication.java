package org.asbeza.frontend;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.asbeza.frontend.utils.SessionManager;

import java.io.IOException;
import java.util.Objects;

public class AsbezaApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        SessionManager session = SessionManager.getInstance();
        String fxmlFile = "landing-page.fxml";

        if (session.isAuthenticated()) {
            String role = session.getUserRole();
            fxmlFile = switch (role.toLowerCase()) {
                case "role_admin" -> "admin/dashboard.fxml";
                case "role_delivery_personnel" -> "delivery/dashboard.fxml";
                default -> "customer/browse.fxml";
            };
        }
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/asbeza/frontend/icons/asbeza-logo.png"))));
        NavigationHandler.initialize(primaryStage);
        NavigationHandler.getInstance().navigateTo(fxmlFile, "Asbeza", null);
    }

}