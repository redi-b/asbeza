package org.asbeza.frontend.responses;

import lombok.Data;
import org.asbeza.frontend.types.Order;

@Data
public class OrderDetailsResponse {

    private Order data;
    private String message;
    private int status;

}
