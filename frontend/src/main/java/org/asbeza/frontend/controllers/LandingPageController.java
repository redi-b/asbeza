package org.asbeza.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.asbeza.frontend.NavigationHandler;

public class LandingPageController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button adminLoginButton;

    @FXML
    private Button deliveryLoginButton;

    private final NavigationHandler navigation = NavigationHandler.getInstance();

    @FXML
    void onLoginButtonClick(ActionEvent event) {
        navigation.navigateTo("customer/login-page.fxml", "Login", null);
    }

    @FXML
    void onRegisterButtonClick(ActionEvent event) {
        navigation.navigateTo("customer/register-page.fxml", "Register", null);
    }

    @FXML
    void onAdminLoginClick(ActionEvent event) {
        navigation.navigateTo("admin/login-page.fxml", "Admin Login", null);
    }

    @FXML
    void onDeliveryLoginClick(ActionEvent event) {
        navigation.navigateTo("delivery/login-page.fxml", "Delivery Login", null);
    }

}
