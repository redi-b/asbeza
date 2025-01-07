package org.asbeza.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.asbeza.frontend.utils.SessionManager;

public class NavigationHandler {
    private static NavigationHandler instance;
    private Stage primaryStage;

    private NavigationHandler() {
        // Private constructor to enforce singleton pattern
    }

    public static void initialize(Stage primaryStage) {
        if (instance == null) {
            instance = new NavigationHandler();
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            instance.primaryStage = primaryStage;
        }
    }

    public static NavigationHandler getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NavigationHandler is not initialized.");
        }
        return instance;
    }

    public Object getUserData() {
        return primaryStage.getUserData();
    }

    public Stage getStage() {
        return primaryStage;
    }

    public void navigateTo(String fxmlFile, String title, Object userData) {
        try {
            SessionManager sessionManager = SessionManager.getInstance();

            // Extract role prefix if present
            String rolePrefix = extractRoleFromFxml(fxmlFile);

            // Allow unrestricted access to "login-page.fxml" or "register-page.fxml"
            if (rolePrefix == null ||
                    fxmlFile.contains("register-page.fxml") || fxmlFile.contains("login-page.fxml")) {
                loadScene(fxmlFile, title, userData);
                return;
            }

            // Verify role if a prefix exists
            if (!sessionManager.getUserRole().equalsIgnoreCase(rolePrefix)) {
                System.err.println("Unauthorized access attempt! Redirecting to login.");
                sessionManager.clearToken();
                redirectToLogin();
                return;
            }

            // Proceed with navigation
            loadScene(fxmlFile, title, userData);
        } catch (Exception e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
        }
    }

    private void loadScene(String fxmlFile, String title, Object userData) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/asbeza/frontend/views/" + fxmlFile));
        Parent root = loader.load();

        Scene oldScene = primaryStage.getScene();
        Scene scene = oldScene == null
                ? new Scene(root, primaryStage.getMinWidth(), primaryStage.getMinHeight())
                : new Scene(root, oldScene.getWidth(), oldScene.getHeight());

        primaryStage.setUserData(userData);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();
    }

    private void redirectToLogin() {
        navigateTo("landing-page.fxml", "Asbeza - Welcome", "Authorization Error");
    }

    private String extractRoleFromFxml(String fxmlFile) {
        if (fxmlFile.startsWith("admin/")) {
            return "role_admin";
        } else if (fxmlFile.startsWith("customer/")) {
            return "role_customer";
        } else if (fxmlFile.startsWith("delivery/")) {
            return "role_delivery_personnel";
        }
        return null; // No role prefix
    }
}
