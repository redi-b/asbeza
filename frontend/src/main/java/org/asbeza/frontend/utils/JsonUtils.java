package org.asbeza.frontend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Method to extract an element by its key from a JSON string
    public static String extractElement(String jsonString, String key) {
        try {
            // Parse the JSON string into a JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Extract the value of the given key
            JsonNode valueNode = rootNode.path(key);  // 'path' avoids exceptions if the key does not exist

            // Return the value as a string if it exists, else return null
            if (!valueNode.isMissingNode()) {
                return valueNode.asText();  // Convert the value to a string
            }
        } catch (Exception e) {
            // Log or handle the exception as needed
            System.err.println(e.getMessage());
        }
        return null;  // Return null if key not found or an exception occurred
    }

    // Method to extract an integer element by its key from a JSON string
    public static Integer extractIntElement(String jsonString, String key) {
        try {
            // Parse the JSON string into a JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Extract the integer value of the given key
            JsonNode valueNode = rootNode.path(key);

            // Return the value as an integer if it exists, else return null
            if (!valueNode.isMissingNode()) {
                return valueNode.asInt();  // Convert the value to an integer
            }
        } catch (Exception e) {
            // Log or handle the exception as needed
            System.err.println(e.getMessage());;
        }
        return null;  // Return null if key not found or an exception occurred
    }

    // Method to extract a boolean element by its key from a JSON string
    public static Boolean extractBooleanElement(String jsonString, String key) {
        try {
            // Parse the JSON string into a JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Extract the boolean value of the given key
            JsonNode valueNode = rootNode.path(key);

            // Return the value as a boolean if it exists, else return null
            if (!valueNode.isMissingNode()) {
                return valueNode.asBoolean();  // Convert the value to a boolean
            }
        } catch (Exception e) {
            // Log or handle the exception as needed
            System.err.println(e.getMessage());
        }
        return null;  // Return null if key not found or an exception occurred
    }

    public static <T> List<T> extractListFromJson(String jsonString, String key, Class<T> clazz) {
        List<T> objectList = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode dataNode = rootNode.path(key); // Use the provided key to access the desired array
            if (dataNode.isArray()) {
                for (JsonNode node : dataNode) {
                    // Convert each element in the array to an object of the specified type
                    T object = objectMapper.treeToValue(node, clazz);
                    objectList.add(object);
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting list from JSON: " + e.getMessage());
        }
        return objectList;
    }

    public static String createProductJson(
            String name, BigDecimal price, String pricePer, String category, int quantity, BigDecimal discountPercentage, String description
    ) {
        return String.format(
                "{\"name\": \"%s\", \"price\": %.2f, \"pricePer\": \"%s\", \"category\": \"%s\", \"stockQuantity\": %d, \"discountPercentage\": %.2f, \"description\": \"%s\"}",
                name, price, pricePer, category, quantity, discountPercentage, description
        );
    }

    public static String createUserJson(String name, String email, String password, String role) {
        return String.format(
                "{\"name\": \"%s\", \"email\": \"%s\", \"password\": \"%s\", \"role\": \"%s\"}",
                name, email, password, role
        );
    }
}
