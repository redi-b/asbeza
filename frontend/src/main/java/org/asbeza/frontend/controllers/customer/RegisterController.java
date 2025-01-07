package org.asbeza.frontend.controllers.customer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.asbeza.frontend.NavigationHandler;
import org.asbeza.frontend.controllers.admin.ErrorController;
import org.asbeza.frontend.forms.Validator;

import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.AuthService;
import org.asbeza.frontend.utils.SessionManager;

import static org.asbeza.frontend.utils.JsonUtils.extractElement;

public class RegisterController extends ErrorController {
    public TextField nameField;
    public TextField emailField;
    public PasswordField passwordField;
    public PasswordField passwordConfirmField;
    public Button registerButton;
    public Label errorLabel;
    public Hyperlink backToHomeLink;

    private final NavigationHandler navigation = NavigationHandler.getInstance();
    private final SessionManager session = SessionManager.getInstance();

    public void initialize() {
        Platform.runLater(() -> nameField.requestFocus());
        registerButton.setOnAction(this::onRegisterButtonClicked);
    }

    @FXML
    private void onBackToHomeClick() {
        navigation.navigateTo("landing-page.fxml", "Asbeza - Welcome", null);
    }

    private void onRegisterButtonClicked(ActionEvent event) {
        registerActionStart();

        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String passwordConfirm = passwordConfirmField.getText();

        String nameError = Validator.validateRequiredField(name, "Name");
        String emailError = Validator.validateEmail(email);
        String passwordError = Validator.validatePassword(password);
        String passwordMatchError = Validator.validatePasswordsMatch(password, passwordConfirm);

        if (nameError != null) {
            setErrorMessage(nameError);
            showError(true);
            registerActionEnd();
            return;
        }
        if (emailError != null) {
            setErrorMessage(emailError);
            showError(true);
            registerActionEnd();
            return;
        }
        if (passwordError != null) {
            setErrorMessage(passwordError);
            showError(true);
            registerActionEnd();
            return;
        }
        if (passwordMatchError != null) {
            setErrorMessage(passwordMatchError);
            showError(true);
            registerActionEnd();
            return;
        }

        AuthService.registerUser(name, email, password, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                showError(false);
                registerActionEnd();
                String jwtToken = extractElement(response, "data");
                session.saveToken(jwtToken);
                navigation.navigateTo("customer/browse.fxml", "Asbeza - Browse Products", null);
            }

            @Override
            public void onFailure(String errorMessage) {
                setErrorMessage(errorMessage);
                showError(true);
                registerActionEnd();
            }
        });
    }

    private void registerActionStart() {
        registerButton.setDisable(true);
        registerButton.setText("Registering ...");
    }

    private void registerActionEnd() {
        registerButton.setDisable(false);
        registerButton.setText("Register");
    }
}
