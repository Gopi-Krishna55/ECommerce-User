package com.ecommerce.user.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginModel {

    private String userName;
    private String email;
    @NotNull(message = "password should not be null")
    private String password;
}
