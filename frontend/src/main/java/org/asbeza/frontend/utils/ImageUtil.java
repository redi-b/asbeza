package org.asbeza.frontend.utils;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

public class ImageUtil {

    public static Image createImage(String input) {
        if (input.startsWith("http")) {
            // If it's a valid URL, return an Image created from the URL
            return new Image(input, true);
        } else if (isBase64(input)) {
            // If it's a valid Base64 string, create an Image from Base64
            return createImageFromBase64(input);
        } else {
            // Input is invalid
            System.err.println("Invalid image input: neither a valid URL nor a Base64 string.");
            return new Image("https://fakeimg.pl/100x100?text=Asbeza", true);
        }
    }

    private static boolean isBase64(String input) {
        // Base64 strings usually contain only alphanumeric characters, +, /, = and must be properly padded
        if (input == null || input.isEmpty()) {
            return false;
        }
        String base64Pattern = "^[A-Za-z0-9+/]+={0,2}$";
        boolean matches = input.matches(base64Pattern);
        try {
            // Verify by decoding the Base64 string
            Base64.getDecoder().decode(input);
            return matches;
        } catch (IllegalArgumentException e) {
            // Invalid Base64 string
            return false;
        }
    }

    private static Image createImageFromBase64(String base64ImageString) {
        try {
            // Decode the Base64 string into a byte array
            byte[] imageBytes = Base64.getDecoder().decode(base64ImageString);

            // Convert the byte array into an Image
            InputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            return new Image(byteArrayInputStream);
        } catch (IllegalArgumentException e) {
            // Handle invalid Base64 string
            System.err.println("Invalid Base64 string.");
            e.printStackTrace();
            return null;
        }
    }
}
