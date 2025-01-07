package org.asbeza.frontend.types;

import lombok.Data;

@Data
public class CartItem {

    private long productId;
    private String productName;
    private int quantity;
    private double productPrice;
    private String image;

}
