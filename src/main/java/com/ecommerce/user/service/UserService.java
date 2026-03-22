package com.ecommerce.user.service;

import com.ecommerce.user.model.LoginModel;
import com.ecommerce.user.model.OtpVerificationModel;
import com.ecommerce.user.model.UserModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    
    ResponseEntity<?> addUser(UserModel userModel);

    ResponseEntity<?> getAllUsers();

    ResponseEntity<?> getUserByUserId(Integer userId);

    ResponseEntity<?> updateUser(UserModel userModel);

    ResponseEntity<?> deleteUser(Long userId);

    ResponseEntity<?> validateUser(LoginModel loginModel);

    ResponseEntity<?> createOtp(String email);

    ResponseEntity<?> verifyOtp(OtpVerificationModel otpVerificationModel);
}
