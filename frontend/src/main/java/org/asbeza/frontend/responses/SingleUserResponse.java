package org.asbeza.frontend.responses;

import lombok.Data;
import org.asbeza.frontend.types.User;

@Data
public class SingleUserResponse {

    private User data;
    private String message;
    private int status;

}
