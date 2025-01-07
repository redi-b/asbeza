package org.asbeza.frontend.responses;

import lombok.Data;
import org.asbeza.frontend.types.CartItem;

import java.util.List;

@Data
public class CartResponse {

    private Long id;
    private Long userId;
    private List<CartItem> items;
    private double totalPrice;
    private String status;

}
