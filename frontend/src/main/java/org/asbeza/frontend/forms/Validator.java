package org.asbeza.frontend.forms;

import java.util.regex.Pattern;

public class Validator {

    // Check if the field is not blank
    public static String validateRequiredField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return fieldName + " cannot be empty.";
        }
        return null; // Validation passed
    }

    // Validate email format using a regex pattern
    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(email).matches()) {
            return "Invalid email format.";
        }
        return null; // Validation passed
    }

    // Validate password strength (for example, at least 8 characters, including one digit)
    public static String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number.";
        }
        return null; // Validation passed
    }

    public static String validatePasswordsMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        return null; // Validation passed
    }

    public static String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Phone number cannot be empty.";
        }

        // Matches phone numbers like: +251912345678, 0912345678
        String phoneRegex = "^(\\+2519\\d{8}|09\\d{8})$";
        Pattern pattern = Pattern.compile(phoneRegex);

        if (!pattern.matcher(phoneNumber).matches()) {
            return "Invalid phone number.";
        }

        return null; // Validation passed
    }

    public static String validateAtLeastOneFieldRequired(String... values) {
        boolean atLeastOneNonEmpty = false;

        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                atLeastOneNonEmpty = true;
                break;
            }
        }

        if (!atLeastOneNonEmpty) {
            return "At least one updatable value is required.";
        }

        return null; // Validation passed
    }
}
