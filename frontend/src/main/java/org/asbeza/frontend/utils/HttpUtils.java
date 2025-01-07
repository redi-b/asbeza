package org.asbeza.frontend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtils {

    // Method for sending POST requests with an optional Bearer token
    public static String sendPostRequest(URI uri, String requestBody, String token) throws Exception {
        return sendRequest(uri, requestBody, "POST", token);
    }

    // Method for sending PUT requests with an optional Bearer token
    public static String sendPutRequest(URI uri, String requestBody, String token) throws Exception {
        return sendRequest(uri, requestBody, "PUT", token);
    }

    // Method for sending PATCH requests with an optional Bearer token
    public static String sendPatchRequest(URI uri, String requestBody, String token) throws Exception {
        return sendRequest(uri, requestBody, "PATCH", token);
    }

    // Method for sending GET requests with an optional Bearer token
    public static String sendGetRequest(URI uri, String token) throws Exception {
        return sendRequest(uri, null, "GET", token);
    }

    // Method for sending DELETE requests with an optional Bearer token
    public static String sendDeleteRequest(URI uri, String token) throws Exception {
        return sendRequest(uri, null, "DELETE", token);
    }

    // Generic method for handling different HTTP methods
    private static String sendRequest(URI uri, String requestBody, String method, String token) throws Exception {
        HttpURLConnection connection = getHttpURLConnection(uri, requestBody, method, token);

        // Get the response code and handle the response accordingly
        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            return readResponse(connection);  // Success
        } else {
            throw new IOException(readErrorResponse(connection));  // Error
        }
    }

    // Method to set up HttpURLConnection with headers and body
    private static HttpURLConnection getHttpURLConnection(URI uri, String requestBody, String method, String token) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod(method);
        connection.setDoOutput(requestBody != null);  // Enable output stream only if there's a body
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        // Add Bearer token to headers if provided
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        // Send the request body if it exists
        if (requestBody != null) {
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        return connection;
    }

    // Method to read the response body using Jackson's ObjectMapper
    private static String readResponse(HttpURLConnection connection) throws Exception {
        try (var inputStream = connection.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(inputStream).toString();
        }
    }

    // Method to read the error response using Jackson's ObjectMapper
    private static String readErrorResponse(HttpURLConnection connection) throws Exception {
        try (var errorStream = connection.getErrorStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(errorStream).toString();
        }
    }

    // Method to send a POST request with multipart/form-data
    public static String sendMultipartPostRequest(URI uri, Map<String, String> formData, File file, String token) throws Exception {
        String boundary = "Boundary-" + System.currentTimeMillis();
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        // Add Bearer token if provided
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        try (OutputStream os = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {

            // Write form data
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
                writer.append(entry.getValue()).append("\r\n");
            }

            // Write file data (if present)
            if (file != null && file.exists()) {
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(file.getName()).append("\"\r\n");
                writer.append("Content-Type: ").append("application/octet-stream").append("\r\n\r\n");
                writer.flush();
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                }
                writer.append("\r\n");
            }

            // End of multipart
            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.flush();
        }

        // Handle response
        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            return readMultipartResponse(connection);
        } else {
            throw new IOException(readMultipartErrResponse(connection));
        }
    }

    // Method to send a PUT request with multipart/form-data
    public static String sendMultipartPutRequest(URI uri, Map<String, String> formData, File file, String token) throws Exception {
        String boundary = "Boundary-" + System.currentTimeMillis();
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        // Add Bearer token if provided
        if (token != null && !token.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }

        try (OutputStream os = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {

            // Write form data
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
                writer.append(entry.getValue()).append("\r\n");
            }

            // Write file data (if present)
            if (file != null && file.exists()) {
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(file.getName()).append("\"\r\n");
                writer.append("Content-Type: ").append("application/octet-stream").append("\r\n\r\n");
                writer.flush();
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                }
                writer.append("\r\n");
            }

            // End of multipart
            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.flush();
        }

        // Handle response
        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            return readMultipartResponse(connection);
        } else {
            throw new IOException(readMultipartErrResponse(connection));
        }
    }


    private static String readMultipartResponse(HttpURLConnection connection) throws Exception {
        try (var inputStream = connection.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String readMultipartErrResponse(HttpURLConnection connection) throws Exception {
        try (var errorStream = connection.getErrorStream()) {
            return new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
