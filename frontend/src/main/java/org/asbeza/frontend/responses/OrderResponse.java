package org.asbeza.frontend.responses;

import lombok.Data;
import org.asbeza.frontend.types.Order;

import java.util.List;

@Data
public class OrderResponse {

    private List<Order> data;
    private String message;
    private int status;

}
