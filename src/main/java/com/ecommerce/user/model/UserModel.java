package com.ecommerce.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel {

    private Long userId;

    private String userName;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String mobileNumber;

    private Boolean active;
}
