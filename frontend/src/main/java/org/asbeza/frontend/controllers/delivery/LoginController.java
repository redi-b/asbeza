package org.asbeza.frontend.controllers.delivery;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.asbeza.frontend.NavigationHandler;
import org.asbeza.frontend.forms.Validator;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.AuthService;

import static org.asbeza.frontend.utils.JsonUtils.extractElement;

public class LoginController extends CommonController {
    
    public Button loginButton;
    public PasswordField passwordField;
    public TextField emailField;
    public Hyperlink backToHomeLink;

    private final NavigationHandler navigation = NavigationHandler.getInstance();

    public void initialize() {
        Platform.runLater(() -> emailField.requestFocus());
        loginButton.setOnAction(this::onLoginButtonClicked);
    }

    @FXML
    private void onBackToHomeClick(ActionEvent event) {
        navigation.navigateTo("landing-page.fxml", "Asbeza - Welcome", null);
    }

    private void onLoginButtonClicked(ActionEvent actionEvent) {
        loginActionLoading();

        String email = emailField.getText();
        String password = passwordField.getText();

        String emailError = Validator.validateEmail(email);
        String passwordError = Validator.validatePassword(password);

        if (emailError != null) {
            setErrorMessage(emailError);
            showError(true);
            loginActionDone();
            return;
        }
        if (passwordError != null) {
            setErrorMessage(passwordError);
            showError(true);
            loginActionDone();
            return;
        }

        AuthService.loginDeliveryPersonnel(email, password, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                String jwtToken = extractElement(response, "token");
                session.saveToken(jwtToken);
                showError(false);
                loginActionDone();
                navigation.navigateTo("delivery/dashboard.fxml", "Asbeza - Active Deliveries", null);
            }

            @Override
            public void onFailure(String errorMessage) {
                setErrorMessage(errorMessage);
                showError(true);
                loginActionDone();
            }
        });
    }

    private void loginActionLoading() {
        loginButton.setDisable(true);
        loginButton.setText("Logging in ...");
    }

    private void loginActionDone() {
        loginButton.setDisable(false);
        loginButton.setText("Log in");
    }
}
