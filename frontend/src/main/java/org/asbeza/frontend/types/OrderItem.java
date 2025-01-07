package org.asbeza.frontend.types;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private long orderId;
    private long productId;
    private String productName;
    private int quantity;
    private BigDecimal productPrice;
}
