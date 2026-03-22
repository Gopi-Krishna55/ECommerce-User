package com.ecommerce.user.controller.impl;

import com.ecommerce.user.controller.UserController;
import com.ecommerce.user.model.LoginModel;
import com.ecommerce.user.model.OtpVerificationModel;
import com.ecommerce.user.model.UserModel;
import com.ecommerce.user.service.UserService;
import com.ecommerce.user.util.TokenGenerationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {

    private static  final Logger logger = LoggerFactory.getLogger(UserControllerImpl.class);

    private  final UserService userService;

    private final TokenGenerationUtil tokenGenerationUtil;

    public UserControllerImpl(UserService userService, TokenGenerationUtil tokenGenerationUtil) {
        this.userService = userService;
        this.tokenGenerationUtil = tokenGenerationUtil;
    }

    @Override
    public ResponseEntity<?> addUser(UserModel userModel, String authorization) {
        if(null == userModel)
            return ResponseEntity.badRequest().body("user data is mandatory");
        return userService.addUser(userModel);
    }

    @Override
    public ResponseEntity<?> getAllUsers(String authorization) {
        return userService.getAllUsers();
    }

    @Override
    public ResponseEntity<?> login(LoginModel loginModel) {
        return userService.validateUser(loginModel);
    }

    @Override
    public ResponseEntity<?> getUserById(Integer userId, String authorization) {
        return userService.getUserByUserId(userId);
    }

    @Override
    public ResponseEntity<?> updateUser(String authorization, UserModel userModel) {
        return userService.updateUser(userModel);
    }

    @Override
    public ResponseEntity<?> deleteUser(String authorization, Long userId) {
        return userService.deleteUser(userId);
    }

    @Override
    public ResponseEntity<?> createOtp(String email) {
        return userService.createOtp(email);
    }

    @Override
    public ResponseEntity<?> verifyOtp(OtpVerificationModel otpVerificationModel) {
        return userService.verifyOtp(otpVerificationModel);
    }
}
