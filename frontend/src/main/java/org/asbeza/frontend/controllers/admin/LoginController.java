package org.asbeza.frontend.controllers.admin;

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

public class LoginController extends ErrorController {
    public TextField emailField;
    public PasswordField passwordField;
    public Button loginButton;
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
        loginActionStart();

        String email = emailField.getText();
        String password = passwordField.getText();

        String emailError = Validator.validateEmail(email);
        String passwordError = Validator.validatePassword(password);

        if (emailError != null) {
            setErrorMessage(emailError);
            showError(true);

            loginActionEnd();
            return;
        }
        if (passwordError != null) {
            setErrorMessage(passwordError);
            showError(true);
            loginActionEnd();
            return;
        }

        AuthService.loginAdmin(email, password, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                String jwtToken = extractElement(response, "token");
                session.saveToken(jwtToken);
                showError(false);
                loginActionEnd();
                navigation.navigateTo("admin/dashboard.fxml", "Asbeza - Admin", null);
            }

            @Override
            public void onFailure(String errorMessage) {
                showError(true);
                setErrorMessage(errorMessage);
                loginActionEnd();
            }
        });
    }

    private void loginActionStart() {
        loginButton.setDisable(true);
        loginButton.setText("Logging in ...");
    }

    private void loginActionEnd() {
        loginButton.setDisable(false);
        loginButton.setText("Log in");
    }
}
