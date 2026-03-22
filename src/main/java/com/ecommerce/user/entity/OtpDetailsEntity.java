package com.ecommerce.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(schema = "ecommerce_user", name = "otp_details")
public class OtpDetailsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    @Id
    private Long id;

    @Column(name = "otp",length = 6,nullable = false)
    private String otp;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "expiry_date", updatable = false)
    private LocalDateTime expiryDate;

    @Column(name = "email")
    private String email;


    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

}
