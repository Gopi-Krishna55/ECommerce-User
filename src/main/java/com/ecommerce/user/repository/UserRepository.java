package com.ecommerce.user.repository;

import com.ecommerce.user.entity.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDetailsEntity,Long> {

    Optional<UserDetailsEntity> findByUserName(String userName);

    Optional<UserDetailsEntity> findByEmail(String userName);
}
