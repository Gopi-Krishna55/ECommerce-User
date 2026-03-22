package com.ecommerce.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OtpVerificationModel {

    private String mobileNumber;
    @NotNull(message = "otp is mandatory")
    private String otp;

    @Email
    private String email;
}
