package org.asbeza.frontend.services;

import org.asbeza.frontend.utils.HttpUtils;
import org.asbeza.frontend.utils.SessionManager;

public class AuthService {

    private static final String REGISTER_EP = "/users/register";
    private static final String C_LOGIN_EP = "/users/login/customer";
    private static final String A_LOGIN_EP = "/users/login/admin";
    private static final String DP_LOGIN_EP = "/users/login/delivery";
    private static final String GET_USER_EP = "/users/admin/get-user/%d";
    private static final String UPDATE_USER_EP = "/users/admin/update-user/%d";
    private static final String GET_USERS_EP = "/users/admin/get-users";

    private static final SessionManager session = SessionManager.getInstance();

    // Register a user and return JWT token if successful
    public static void registerUser(String name, String email, String password, ApiRequestService.ApiRequestCallback callback) {
        String requestBody = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}", name, email, password);
        ApiRequestService.makeApiRequest(REGISTER_EP, requestBody, null, callback, HttpUtils::sendPostRequest);
    }

    // Login a user and return JWT token if successful
    public static void loginUser(String email, String password, ApiRequestService.ApiRequestCallback callback) {
        String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        ApiRequestService.makeApiRequest(C_LOGIN_EP, requestBody, null, callback, HttpUtils::sendPostRequest);
    }

    // Login a user and return JWT token if successful
    public static void loginAdmin(String email, String password, ApiRequestService.ApiRequestCallback callback) {
        String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        ApiRequestService.makeApiRequest(A_LOGIN_EP, requestBody, null, callback, HttpUtils::sendPostRequest);
    }

    // Login a user and return JWT token if successful
    public static void loginDeliveryPersonnel(String email, String password, ApiRequestService.ApiRequestCallback callback) {
        String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        ApiRequestService.makeApiRequest(DP_LOGIN_EP, requestBody, null, callback, HttpUtils::sendPostRequest);
    }

    // Get user by id for admin
    public static void getUserById(Long userId, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                String.format(GET_USER_EP, userId),
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendGetRequest(url, optionalToken)
        );
    }

    public static void updateUser(Long userId, String userJson, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                String.format(UPDATE_USER_EP, userId),  // Adjust endpoint based on actual API
                userJson,
                token,
                callback,
                HttpUtils::sendPutRequest
        );
    }



    // Get all users for admin
    public static void getAllUsers(ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();
        ApiRequestService.makeApiRequest(
                GET_USERS_EP, null, token, callback,
                ((uri, requestBody, token1) -> HttpUtils.sendGetRequest(uri, token))
        );
    }
}
