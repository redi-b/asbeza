package org.asbeza.frontend.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.asbeza.frontend.responses.UserResponse;
import org.asbeza.frontend.services.ApiRequestService;
import org.asbeza.frontend.services.AuthService;
import org.asbeza.frontend.types.Role;
import org.asbeza.frontend.types.User;
import org.w3c.dom.CDATASection;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageUsersController extends CommonController {
    public Label lblUserName;
    @FXML
    private VBox usersContainer;

    public void initialize() {
        lblUserName.setText(session.getUserName());

        Platform.runLater(this::fetchAndUpdateUsers);
    }

    private void loadUsers(List<User> users) {
        usersContainer.getChildren().clear();

        if (users.isEmpty()) {
            showEmptyMessage("No users found.", usersContainer);
            return;
        }

        // Add each user as a card to the VBox
        for (User user : users) {
            usersContainer.getChildren().add(createUserCard(user));
        }
    }

    private void fetchAndUpdateUsers() {
        showError(false);
        showLoading(true, usersContainer);

        AuthService.getAllUsers(new ApiRequestService.ApiRequestCallback() {
            @Override
            public void onSuccess(String response) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                try {
                    UserResponse userResponse = objectMapper.readValue(response, UserResponse.class);
                    loadUsers(userResponse.getData());
                    showLoading(false, usersContainer);
                } catch (JsonProcessingException e) {
                    showLoading(false, usersContainer);
                    setErrorMessage(e.getMessage());
                    showError(true);
                }

            }

            @Override
            public void onFailure(String errorMessage) {
                showLoading(false, usersContainer);
                setErrorMessage(errorMessage);
                showError(true);
            }
        });

    }

    // Method to create a user card
    private HBox createUserCard(User user) {
        HBox card = new HBox(15);
        card.setStyle("-fx-padding: 10; -fx-border-color: #dddddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefHeight(120);

        // User Details
        VBox details = new VBox(5);

        // Hyperlink for the user's name
        Hyperlink userNameLink = new Hyperlink(user.getName());
        userNameLink.setStyle("-fx-text-fill: #007bff;"); // Make the text look like a link

        // Set event handler to navigate to the user's details/edit page when clicked
        userNameLink.setOnAction(event -> navigateToEditPage(user.getId()));

        Label userEmail = new Label("Email: " + user.getEmail());
        userEmail.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        Label userRole = new Label("Role: " + user.getRole());
        userRole.setStyle("-fx-font-size: 14px;");

        HBox dateContainer = new HBox(10);

        Label userCreatedAt = new Label(
                "Created on: " + user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a"))
        );
        userCreatedAt.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Label userUpdatedAt = new Label(
                "Last Updated: " + user.getUpdatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a"))
        );
        userUpdatedAt.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        dateContainer.getChildren().addAll(userUpdatedAt, spacer, userCreatedAt);

        // Add user details to the VBox
        details.getChildren().addAll(userNameLink, userEmail, userRole, dateContainer);

        // Add the details to the card
        HBox.setHgrow(details, Priority.ALWAYS);  // Allow the details section to expand
        card.getChildren().addAll(details);

        return card;
    }

    // Method to navigate to the "Edit User" page
    private void navigateToEditPage(Long userId) {
        navigation.navigateTo("admin/edit-user.fxml", "Asbeza - Admin - Edit User", userId);
    }
}
