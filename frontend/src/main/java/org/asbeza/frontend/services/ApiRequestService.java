package org.asbeza.frontend.services;

import javafx.application.Platform;

import java.net.ConnectException;
import java.net.URI;

import static org.asbeza.frontend.utils.JsonUtils.extractElement;

public class ApiRequestService {

    // Generic method to make API requests (POST, PUT, GET, DELETE, etc.) with optional Bearer token
    public static void makeApiRequest(String endpoint, String requestBody, String token, ApiRequestCallback callback, ApiRequestMethod apiRequestMethod) {
        // Run the network request on a separate thread
        new Thread(() -> {
            try {
                URI uri = URI.create("http://localhost:8080/api" + endpoint);  // Construct full URL with base API
                String response = apiRequestMethod.sendRequest(uri, requestBody, token);  // Send request with optional token

                // Use Platform.runLater to run on the JavaFX application thread
                Platform.runLater(() -> {
                    if (response != null) {
                        callback.onSuccess(response);
                    } else {
                        callback.onFailure("Request failed, please try again.");
                    }
                });
            } catch (ConnectException e) {
                Platform.runLater(
                        () -> callback.onFailure(
                                "Can't connect to server. Please try again later!"
                        )
                );
            } catch (Exception e) {
                // Handle any errors and update the UI on the JavaFX thread.
                // Extract the error message from the server response and fire the callback
                e.printStackTrace();
                Platform.runLater(
                        () -> callback.onFailure(
                                extractElement(e.getMessage(), "error")
                        )
                );
            }
        }).start();  // Start the thread
    }

    // Callback interface for handling success or failure
    public interface ApiRequestCallback {
        void onSuccess(String response);  // Success response (e.g., JWT token or API response)

        void onFailure(String errorMessage);  // Failure message
    }

    // Functional interface for sending different types of HTTP requests (POST, PUT, GET, DELETE, etc.)
    @FunctionalInterface
    public interface ApiRequestMethod {
        String sendRequest(URI uri, String requestBody, String token) throws Exception;
    }
}
