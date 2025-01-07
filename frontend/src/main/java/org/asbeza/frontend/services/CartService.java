package org.asbeza.frontend.services;

import org.asbeza.frontend.utils.HttpUtils;
import org.asbeza.frontend.utils.SessionManager;

public class CartService {

    private static final String GET_CART_EP = "/cart/get-cart";
    private static final String UPDATE_CART_EP = "/cart/update-cart";
    private static final String ADD_ITEM_EP = "/cart/add-item";
    private static final String REMOVE_ITEM_EP = "/cart/remove-item";
    private static final String UPDATE_QUANTITY_EP = "/cart/update-quantity";
    private static final String CLEAR_CART_EP = "/cart/clear";

    private static final SessionManager session = SessionManager.getInstance();

    // 1. Get User Cart
    public static void getUserCart(ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                GET_CART_EP,
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendGetRequest(url, optionalToken)
        );
    }

    // 2. Update Entire Cart
    public static void updateCart(String cartJson, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                UPDATE_CART_EP,
                cartJson,
                token,
                callback,
                HttpUtils::sendPutRequest
        );
    }

    // 3. Add Single Item to Cart
    public static void addCartItem(Long productId, int quantity, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        String queryParams = String.format("?productId=%d&quantity=%d", productId, quantity);

        ApiRequestService.makeApiRequest(
                ADD_ITEM_EP + queryParams,
                null,
                token,
                callback,
                HttpUtils::sendPostRequest
        );
    }

    // 4. Remove Single Item from Cart
    public static void removeCartItem(Long productId, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        String queryParams = String.format("?productId=%d", productId);

        ApiRequestService.makeApiRequest(
                REMOVE_ITEM_EP + queryParams,
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendDeleteRequest(url, optionalToken)
        );
    }

    // 5. Update Quantity of Single Item
    public static void updateCartItemQuantity(Long productId, int quantity, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        String queryParams = String.format("?productId=%d&quantity=%d", productId, quantity);

        ApiRequestService.makeApiRequest(
                UPDATE_QUANTITY_EP + queryParams,
                null,
                token,
                callback,
                HttpUtils::sendPutRequest
        );
    }

    // 6. Clear Cart
    public static void clearCart(Long userId, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        String queryParams = String.format("?userId=%d", userId);

        ApiRequestService.makeApiRequest(
                CLEAR_CART_EP + queryParams,
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendDeleteRequest(url, optionalToken)
        );
    }
}
