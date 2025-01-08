package com.ecommerce.asbeza.services;

import com.ecommerce.asbeza.dtos.*;
import com.ecommerce.asbeza.models.*;
import com.ecommerce.asbeza.repositories.*;
import com.ecommerce.asbeza.types.OrderStatus;
import com.ecommerce.asbeza.types.Role;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    // Create Order from Cart
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        User user = getAuthenticatedUser();

        // Get user cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order! Cart is empty.");
        }

        // Validate total price
        double totalPrice = cart.getItems().stream()
                .mapToDouble(item -> item.getSubtotal().doubleValue())
                .sum();

        // Create order
        Order order = new Order();
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getSubtotal());
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setAddress(orderRequest.getAddress());
        order.setPhoneNumber(orderRequest.getPhoneNumber());

        User deliveryPersonnel = assignDeliveryPersonnel();
        if (deliveryPersonnel != null) {
            order.setDeliveryPersonnel(deliveryPersonnel);
        }

        // Clear the cart's items
        cart.getItems().clear();
        cart.setTotalPrice(0);

        cartRepository.save(cart);

        // Save and return order response
        order = orderRepository.save(order);

        // Send email notification
//        try {
//            String emailBody = generateEmailBody(order); // Generate email content
//            emailService.sendEmail(user.getEmail(), "Order Confirmation", emailBody);
//        } catch (MessagingException e) {
//            // Log the error and proceed without interrupting order creation
//            System.err.println("Error sending email: " + e.getMessage());
//        }

        return modelMapper.map(order, OrderResponseDTO.class);
    }


    // Get Order by ID
    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = findOrderById(orderId);
        return modelMapper.map(order, OrderResponseDTO.class);
    }

    // Get All Orders for User
    public List<OrderResponseDTO> getOrdersForUser() {
        User user = getAuthenticatedUser();
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Update Order Status (Admin or Delivery Personnel Only)
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderUpdateDTO orderUpdateDTO) {
        Order order = findOrderById(orderId);
        User user = getAuthenticatedUser();

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.DELIVERY_PERSONNEL) {
            throw new AccessDeniedException("Only admin or delivery personnel can update order status.");
        }

        order.setStatus(orderUpdateDTO.getStatus());
        order = orderRepository.save(order);
        return modelMapper.map(order, OrderResponseDTO.class);
    }

    // Cancel Order (Customer Only)
    public void cancelOrder(Long orderId) {
        Order order = findOrderById(orderId);
        User user = getAuthenticatedUser();

        if (!user.getId().equals(order.getUser().getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only cancel your own orders.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only pending orders can be canceled.");
        }

        orderRepository.delete(order);
    }

    // Find Order by ID and check permissions
    private Order findOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        User user = getAuthenticatedUser();
        if (!user.getId().equals(order.getUser().getId())
                && user.getRole() != Role.ADMIN
                && !user.getId().equals(order.getDeliveryPersonnel().getId())) {
            throw new AccessDeniedException("Access denied to this order.");
        }
        return order;
    }

    // Get all products
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .sorted(Comparator.comparing(Order::getUpdatedAt).reversed())
                .map(order -> modelMapper.map(order, OrderResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Get Orders for Specific Delivery Personnel
    public List<OrderResponseDTO> getOrdersForDeliveryPersonnel(Long deliveryPersonnelId) {
        User deliveryPersonnel = userRepository.findById(deliveryPersonnelId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery personnel not found."));
        if (!deliveryPersonnel.getRole().equals(Role.DELIVERY_PERSONNEL)) {
            throw new IllegalArgumentException("Specified user is not a delivery personnel.");
        }

        List<Order> orders = orderRepository.findByDeliveryPersonnel(deliveryPersonnel);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Update Delivery Personnel
    public OrderResponseDTO updateDeliveryPersonnel(Long orderId, Long deliveryPersonnelId) {
        Order order = findOrderById(orderId);

        // Check if authenticated user is an admin
        User user = getAuthenticatedUser();
        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admins can assign delivery personnel.");
        }

        // Fetch and validate delivery personnel
        User deliveryPersonnel = userRepository.findById(deliveryPersonnelId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery personnel not found."));
        if (!deliveryPersonnel.getRole().equals(Role.DELIVERY_PERSONNEL)) {
            throw new IllegalArgumentException("Specified user is not a delivery personnel.");
        }

        order.setDeliveryPersonnel(deliveryPersonnel);
        order = orderRepository.save(order);
        return modelMapper.map(order, OrderResponseDTO.class);
    }

    // Authenticate User
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private User assignDeliveryPersonnel() {
        // Fetch all users with the DELIVERY_PERSONNEL role
        List<User> deliveryPersonnelList = userRepository.findByRole(Role.DELIVERY_PERSONNEL);

        if (deliveryPersonnelList.isEmpty()) {
            return null; // Return null if no delivery personnel are available
        }

        // Example logic: Assign the first available delivery personnel (can be enhanced)
        return deliveryPersonnelList.stream()
                .min(Comparator.comparingInt(orderRepository::countByDeliveryPersonnel))
                .orElse(null);
    }

    private String generateEmailBody(Order order) {
        String paymentLink = "https://payment-processor.netlify.app?orderId=" + order.getId();

        return """
            <html>
                <body>
                    <h2>Order Confirmation</h2>
                    <p>Dear %s,</p>
                    <p>Thank you for your order! Here are your order details:</p>
                    <ul>
                        <li>Order ID: %d</li>
                        <li>Total Price: $%.2f</li>
                        <li>Status: %s</li>
                        <li>Delivery Address: %s</li>
                    </ul>
                    <p>
                        <strong>Important:</strong> To complete your order,\s
                        please make the payment within <strong>2 hours</strong>.\s
                        If payment is not completed in time, your order will be automatically cancelled.
                    </p>
                    <p>You can make the payment by clicking the link below:</p>
                    <a href="%s">Complete Payment</a>
                    <p>We look forward to serving you!</p>
                    <p>Thank you for shopping with us!</p>
                </body>
            </html>
        """.formatted(
                order.getUser().getName(),
                order.getId(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getAddress(),
                paymentLink
        );
    }
}
