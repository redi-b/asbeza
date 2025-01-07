package org.asbeza.frontend.responses;

import lombok.Data;
import org.asbeza.frontend.types.User;

import java.util.List;

@Data
public class UserResponse {

    private List<User> data;
    private String message;
    private int status;

}
