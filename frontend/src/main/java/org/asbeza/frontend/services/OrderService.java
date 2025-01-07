package org.asbeza.frontend.services;

import org.asbeza.frontend.utils.HttpUtils;
import org.asbeza.frontend.utils.SessionManager;

public class OrderService {

    private static final String CREATE_ORDER_EP = "/orders";
    private static final String GET_ORDER_EP = "/orders";
    private static final String GET_ORDER_BY_ID_EP = "/orders";
    private static final String UPDATE_ORDER_STATUS_EP = "/orders/%d/status";
    private static final String CANCEL_ORDER_EP = "/orders/%d";
    private static final String GET_ALL_ORDERS_EP = "/orders/get-orders";
    private static final String UPDATE_DELIVERY_PERSONNEL_EP = "/orders/%d/delivery-personnel";
    private static final String GET_ORDERS_BY_DELIVERY_PERSONNEL_EP = "/orders/get-orders-by-delivery-personnel/%d";

    private static final SessionManager session = SessionManager.getInstance();

    // 1. Create an Order
    public static void createOrder(String address, String phoneNumber, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();
        String requestBody = String.format("{\"address\":\"%s\",\"phoneNumber\":\"%s\"}", address, phoneNumber);

        ApiRequestService.makeApiRequest(
                CREATE_ORDER_EP,
                requestBody,
                token,
                callback,
                HttpUtils::sendPostRequest
        );
    }

    // 2. Get All Orders for Logged-in User
    public static void getOrdersForUser(ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                GET_ORDER_EP,
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendGetRequest(url, optionalToken)
        );
    }

    // 3. Get Order by ID
    public static void getOrderById(Long orderId, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                GET_ORDER_BY_ID_EP + "/" + orderId,
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendGetRequest(url, optionalToken)
        );
    }

    // 4. Update Order Status (Admin or Delivery Personnel Only)
    public static void updateOrderStatus(Long orderId, String status, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        String url = String.format(UPDATE_ORDER_STATUS_EP, orderId);
        String requestBody = String.format("{\"status\":\"%s\"}", status);

        ApiRequestService.makeApiRequest(
                url,
                requestBody,
                token,
                callback,
                HttpUtils::sendPutRequest
        );
    }

    // 5. Cancel Order (Customer Only)
    public static void cancelOrder(Long orderId, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                String.format(CANCEL_ORDER_EP, orderId),
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendDeleteRequest(url, optionalToken)
        );
    }

    // 6. Get all orders
    public static void getAllOrders(ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        ApiRequestService.makeApiRequest(
                GET_ALL_ORDERS_EP,
                null,
                token,
                callback,
                (url, body, optionalToken) -> HttpUtils.sendGetRequest(url, optionalToken)
        );
    }

    // 7. Update Delivery Personnel
    public static void updateDeliveryPersonnel(Long orderId, Long deliveryPersonnelId, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        String url = String.format(UPDATE_DELIVERY_PERSONNEL_EP, orderId);
        String requestBody = String.format("{\"deliveryPersonnelId\":%d}", deliveryPersonnelId);

        ApiRequestService.makeApiRequest(
                url,
                requestBody,
                token,
                callback,
                HttpUtils::sendPatchRequest
        );
    }

    // 8. Get Orders by Delivery Personnel
    public static void getOrdersByDeliveryPersonnel(Long deliveryPersonnelId, ApiRequestService.ApiRequestCallback callback) {
        String token = session.getToken();

        String url = String.format(GET_ORDERS_BY_DELIVERY_PERSONNEL_EP, deliveryPersonnelId);

        ApiRequestService.makeApiRequest(
                url,
                null,
                token,
                callback,
                (urlFinal, body, optionalToken) -> HttpUtils.sendGetRequest(urlFinal, optionalToken)
        );
    }
}
