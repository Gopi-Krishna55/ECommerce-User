package com.ecommerce.user.repository;

import com.ecommerce.user.entity.OtpDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<OtpDetailsEntity,Long> {

    OtpDetailsEntity findByEmail(String email);
}
