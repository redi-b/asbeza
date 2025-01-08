package org.asbeza.frontend.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.asbeza.frontend.forms.Validator;
import org.asbeza.frontend.responses.SingleUserResponse;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.AuthService;
import org.asbeza.frontend.types.Role;
import org.asbeza.frontend.types.User;
import org.asbeza.frontend.utils.JsonUtils;

import java.util.Arrays;


public class EditUserController extends CommonController {

    @FXML public Label lblUserName;
    @FXML public Button btnSaveUser;
    @FXML public PasswordField txtPassword;
    @FXML private TextField txtUserName;
    @FXML private TextField txtEmail;
    @FXML public ComboBox<String> cmbRole;

    private Long userId;

    public void initialize() {
        lblUserName.setText(session.getUserName());

        // Retrieve the userId from userData (it can be null, so we check before using it)
        Platform.runLater(() -> {
            Object userData = navigation.getStage().getUserData();

            if (userData instanceof Long) {
                userId = (Long) userData;
                loadUserDetails(userId);  // Load user details using the userId
            } else {
                setErrorMessage("No user data provided or invalid data");
                showError(true);
            }

            Arrays.stream(Role.values()).forEach((role) -> {
                cmbRole.getItems().add(role.name());
            });
        });
    }

    private void loadUserDetails(Long userId) {
        if (userId == null) {
            setErrorMessage("No user data provided or invalid data");
            showError(true);
            return;
        }

        AuthService.getUserById(userId, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                try {
                    SingleUserResponse userResponse = objectMapper.readValue(response, SingleUserResponse.class);
                    fillDetails(userResponse.getData());
                } catch (JsonProcessingException e) {
                    setErrorMessage(e.getMessage());
                    showError(true);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                setErrorMessage(errorMessage);
                showError(true);
            }
        });
    }

    private void fillDetails(User user) {
        // Populate the fields with the user data
        txtUserName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        cmbRole.getSelectionModel().select(user.getRole());
    }

    @FXML
    private void handleSaveUser() {
        saveActionStart();

        // Collect data from input fields
        String userName = txtUserName.getText(),
                email = txtEmail.getText(),
                password = txtPassword.getText(),
                role = cmbRole.getSelectionModel().getSelectedItem();

        // Validate fields
        String error = Validator.validateAtLeastOneFieldRequired(userName, email, role, password);
        if (error != null) {
            setErrorMessage(error);
            showError(true);
            saveActionEnd();
            return;
        }

        String userJson = JsonUtils.createUserJson(userName, email, password, role);

        AuthService.updateUser(userId, userJson, new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                clearFields();
                showError(false);
                saveActionEnd();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(btnSaveUser.getScene().getWindow());
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("User updated successfully!");

                alert.showAndWait();
            }

            @Override
            public void onFailure(String errorMessage) {
                saveActionEnd();
                setErrorMessage(errorMessage);
                showError(true);
            }
        });
    }

    private void saveActionStart() {
        btnSaveUser.setDisable(true);
        btnSaveUser.setText("Saving User ...");
    }

    private void saveActionEnd() {
        btnSaveUser.setDisable(false);
        btnSaveUser.setText("Save User");
    }

    private void clearFields() {
        loadUserDetails(userId);
    }
}
