package org.asbeza.frontend.services;

import org.asbeza.frontend.utils.HttpUtils;
import org.asbeza.frontend.utils.SessionManager;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ProductService {

    private static final String GET_ALL_PRODUCTS_EP = "/products/get-products";
    private static final String GET_PRODUCT_BY_ID_EP = "/products/%d";
    private static final String CREATE_PRODUCT_EP = "/admin/products/add-product";
    private static final String UPDATE_PRODUCT_EP = "/admin/products/%d";
    private static final String DELETE_PRODUCT_EP = "/admin/products/%d";

    private static final SessionManager session = SessionManager.getInstance();

    // Create a product
    public static void createProduct(String productJson, File imageFile, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        // Define the API request method for multipart/form-data
        ApiRequestService.ApiRequestMethod apiRequestMethod = (uri, requestBody, authToken) -> {
            Map<String, String> formData = new HashMap<>();
            formData.put("product", requestBody); // Add product JSON to form data
            return HttpUtils.sendMultipartPostRequest(uri, formData, imageFile, authToken);
        };

        // Use makeApiRequest to send the request
        ApiRequestService.makeApiRequest(CREATE_PRODUCT_EP, productJson, token, callback, apiRequestMethod);
    }

    // Get all products
    public static void getAllProducts(ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                GET_ALL_PRODUCTS_EP,
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendGetRequest(url, optionalToken)
        );
    }

    // Get product by ID
    public static void getProductById(Long productId, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                String.format(GET_PRODUCT_BY_ID_EP, productId),
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendGetRequest(url, optionalToken)
        );
    }

    // Update a product
    public static void updateProduct(Long productId, String productJson, File imageFile, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        // Define the API request method for multipart/form-data
        ApiRequestService.ApiRequestMethod apiRequestMethod = (uri, requestBody, authToken) -> {
            Map<String, String> formData = new HashMap<>();
            formData.put("product", requestBody); // Add product JSON to form data
            return HttpUtils.sendMultipartPutRequest(uri, formData, imageFile, authToken);
        };

        // Use makeApiRequest to send the request
        ApiRequestService.makeApiRequest(
                String.format(UPDATE_PRODUCT_EP, productId),
                productJson,
                token,
                callback,
                apiRequestMethod
        );
    }


    // Delete a product
    public static void deleteProduct(Long productId, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                String.format(DELETE_PRODUCT_EP, productId),
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendDeleteRequest(url, optionalToken)
        );
    }
}
