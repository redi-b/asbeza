package org.asbeza.frontend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class SessionManager {

    private static final String TOKEN_FILE =  getTokenFilePath(); // File to store the token
    private static SessionManager instance;

    private SessionManager() {
        // Singleton
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Save token to file
    public void saveToken(String token) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(TOKEN_FILE))) {
            writer.write(token);
        } catch (IOException e) {
            System.err.println("Failed to save token: " + e.getMessage());
        }
    }

    // Load token from file
    public String getToken() {
        try {
            if (Files.exists(Paths.get(TOKEN_FILE))) {
                return Files.readString(Paths.get(TOKEN_FILE)).trim();
            }
        } catch (IOException e) {
            System.err.println("Failed to read token: " + e.getMessage());
        }
        return null;
    }

    // Remove token file
    public void clearToken() {
        try {
            Files.deleteIfExists(Paths.get(TOKEN_FILE));
        } catch (IOException e) {
            System.err.println("Failed to delete token file: " + e.getMessage());
        }
    }

    // Check if a user is authenticated
    public boolean isAuthenticated() {
        try {
            return decodeToken() != null;
        } catch (Exception e) {
            System.err.println("Token verification failed: " + e.getMessage());
            clearToken(); // Clear invalid token
            return false;
        }
    }

    // Decode JWT without signature verification
    private Claims decodeToken() {
        String token = getToken();
        if (token == null) {
            return null; // No token saved
        }

        try {
            String[] jwtParts = token.split("\\.");
            if (jwtParts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format");
            }

            String payload = new String(Base64.getUrlDecoder().decode(jwtParts[1]));
            return Jwts.parserBuilder()
                    .build()
                    .parseClaimsJwt(jwtParts[0] + "." + jwtParts[1] + ".")
                    .getBody();
        } catch (Exception e) {
            System.err.println("Failed to decode JWT: " + e.getMessage());
            throw e;
        }
    }

    // Extract user ID from the token
    public Long getUserId() {
        try {
            Claims claims = decodeToken();
            return claims != null ? claims.get("userId", Long.class) : null;
        } catch (Exception e) {
            System.err.println("Failed to extract user ID: " + e.getMessage());
            clearToken(); // Clear invalid token
            return null;
        }
    }

    // Extract name of user from the token
    public String getUserName() {
        try {
            Claims claims = decodeToken();
            return claims != null ? claims.get("name", String.class) : null;
        } catch (Exception e) {
            System.err.println("Failed to extract user name: " + e.getMessage());
            clearToken(); // Clear invalid token
            return null;
        }
    }

    // Extract user role from the token
    public String getUserRole() {
        try {
            Claims claims = decodeToken();
            return claims != null ? claims.get("role", String.class) : null;
        } catch (Exception e) {
            System.err.println("Failed to extract user role: " + e.getMessage());
            clearToken(); // Clear invalid token
            return null;
        }
    }

    // Extract email from the token
    public String getEmail() {
        try {
            Claims claims = decodeToken();
            return claims != null ? claims.get("email", String.class) : null;
        } catch (Exception e) {
            System.err.println("Failed to extract email: " + e.getMessage());
            clearToken(); // Clear invalid token
            return null;
        }
    }

    // Extract custom claim from the token
    public <T> T getCustomClaim(String claimKey, Class<T> claimType) {
        try {
            Claims claims = decodeToken();
            return claims != null ? claims.get(claimKey, claimType) : null;
        } catch (Exception e) {
            System.err.println("Failed to extract custom claim '" + claimKey + "': " + e.getMessage());
            clearToken(); // Clear invalid token
            return null;
        }
    }

    // Determine a secure storage location for the token (based on OS)
    private static String getTokenFilePath() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        // Linux/macOS: Use home directory or a config directory
        if (os.contains("nix") || os.contains("mac")) {
            return userHome + File.separator + ".asbeza" + File.separator + "session_token.txt";
        }
        // Windows: Use local app data
        else if (os.contains("win")) {
            return System.getenv("LOCALAPPDATA") + File.separator + "Asbeza" + File.separator + "session_token.txt";
        }
        return userHome + File.separator + "session_token.txt"; // Default path
    }
}
