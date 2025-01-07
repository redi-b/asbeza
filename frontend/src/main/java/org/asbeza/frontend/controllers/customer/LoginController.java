package org.asbeza.frontend.controllers.customer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.asbeza.frontend.NavigationHandler;
import org.asbeza.frontend.forms.Validator;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.AuthService;
import org.asbeza.frontend.utils.SessionManager;

import static org.asbeza.frontend.utils.JsonUtils.extractElement;

public class LoginController {
    public TextField emailField;
    public PasswordField passwordField;
    public Button loginButton;
    public Label errorLabel;
    public Hyperlink backToHomeLink;

    private final NavigationHandler navigation = NavigationHandler.getInstance();
    private final SessionManager session = SessionManager.getInstance();

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
            showError(emailError);
            loginActionDone();
            return;
        }
        if (passwordError != null) {
            showError(passwordError);
            loginActionDone();
            return;
        }

        AuthService.loginUser(email, password, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                String jwtToken = extractElement(response, "token");
                session.saveToken(jwtToken);
                hideError();
                loginActionDone();
                navigation.navigateTo("customer/browse.fxml", "Asbeza - Browse Products", null);
            }

            @Override
            public void onFailure(String errorMessage) {
                showError(errorMessage);
                loginActionDone();
            }
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
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
