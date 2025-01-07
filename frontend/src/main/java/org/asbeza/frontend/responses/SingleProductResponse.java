package org.asbeza.frontend.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asbeza.frontend.types.Product;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleProductResponse {

    private Product data;
    private String message;
    private int status;

}