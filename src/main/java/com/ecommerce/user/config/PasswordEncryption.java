package com.ecommerce.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncryption {

    @Bean
    //to create password encryption
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //to decrypt the password
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder().matches(rawPassword, encodedPassword);
    }
}
