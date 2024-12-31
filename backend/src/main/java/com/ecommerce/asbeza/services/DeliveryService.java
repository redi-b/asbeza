package com.ecommerce.asbeza.services;

import com.ecommerce.asbeza.dtos.DeliveryDTO;
import com.ecommerce.asbeza.exceptions.ResourceNotFoundException;
import com.ecommerce.asbeza.models.Delivery;
import com.ecommerce.asbeza.types.DeliveryStatus;
import com.ecommerce.asbeza.models.Order;
import com.ecommerce.asbeza.repositories.DeliveryRepository;
import com.ecommerce.asbeza.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ModelMapper modelMapper;

    public DeliveryDTO scheduleDelivery(Long orderId, String deliveryAddress) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryAddress(deliveryAddress);
        delivery.setScheduledDate(LocalDate.now().plusDays(3)); // Assume 3 days for delivery
        delivery.setDeliveryStatus(DeliveryStatus.Scheduled);

        Delivery savedDelivery = deliveryRepository.save(delivery);

        return modelMapper.map(savedDelivery, DeliveryDTO.class);
    }

    public List<DeliveryDTO> getDeliveriesByOrder(Long orderId) {
        List<Delivery> deliveries = deliveryRepository.findByOrderId(orderId);

        if (deliveries.isEmpty()) {
            throw new ResourceNotFoundException("Delivery", "orderId", orderId);
        }

        return deliveries.stream()
                .map(delivery -> modelMapper.map(delivery, DeliveryDTO.class))
                .collect(Collectors.toList());
    }

    public DeliveryDTO updateDeliveryStatus(Long deliveryId, DeliveryStatus deliveryStatus) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery", "deliveryId", deliveryId));

        delivery.setDeliveryStatus(deliveryStatus);

        Delivery updatedDelivery = deliveryRepository.save(delivery);

        return modelMapper.map(updatedDelivery, DeliveryDTO.class);
    }

    public List<DeliveryDTO> getAllDeliveries() {
        List<Delivery> deliveries = deliveryRepository.findAll();

        if (deliveries.isEmpty()) {
            throw new ResourceNotFoundException("Delivery", "all deliveries", 0L);
        }

        return deliveries.stream()
                .map(delivery -> modelMapper.map(delivery, DeliveryDTO.class))
                .collect(Collectors.toList());
    }
}
