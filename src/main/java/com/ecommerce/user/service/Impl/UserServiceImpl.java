package com.ecommerce.user.service.Impl;

import com.ecommerce.user.config.PasswordEncryption;
import com.ecommerce.user.entity.OtpDetailsEntity;
import com.ecommerce.user.entity.UserDetailsEntity;
import com.ecommerce.user.model.LoginModel;
import com.ecommerce.user.model.OtpVerificationModel;
import com.ecommerce.user.model.UserModel;
import com.ecommerce.user.repository.OtpRepository;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.service.UserService;
import com.ecommerce.user.util.TokenGenerationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncryption passwordEncryption;
    private final TokenGenerationUtil tokenGenerationUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private static final SecureRandom secureRandom = new SecureRandom();
    private final OtpRepository otpRepository;
    private final OtpProducer otpProducer;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncryption passwordEncryption,
            TokenGenerationUtil tokenGenerationUtil,
            AuthenticationManager authenticationManager,
            ModelMapper modelMapper, EmailService emailService, OtpRepository otpRepository, OtpProducer otpProducer
    ) {
        this.userRepository = userRepository;
        this.passwordEncryption = passwordEncryption;
        this.tokenGenerationUtil = tokenGenerationUtil;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
        this.otpRepository = otpRepository;
        this.otpProducer = otpProducer;
    }

    @Override
    public ResponseEntity<String> addUser(UserModel userModel) {

        Optional<UserDetailsEntity> existingUser =
                userRepository.findByUserName(userModel.getUserName())
                        .or(() -> userRepository.findByEmail(userModel.getEmail()));

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User already exists");
        }

        // Map DTO → Entity
        UserDetailsEntity entity =
                modelMapper.map(userModel, UserDetailsEntity.class);

        // Encode password correctly
        entity.setPassword(
                passwordEncryption.passwordEncoder()
                        .encode(userModel.getPassword())
        );

        entity.setActive(true);

        userRepository.save(entity);

        return ResponseEntity.ok("User saved successfully");
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        List<UserDetailsEntity> users = userRepository.findAll();

        List<UserModel> userModels = users.stream().map(user -> {
            UserModel model = modelMapper.map(user, UserModel.class);
            model.setPassword(null);
            return model;
        }).toList();

        return ResponseEntity.ok(userModels);
    }


    @Override
    public ResponseEntity<?> getUserByUserId(Integer userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User ID must be provided");
        }

        Optional<UserDetailsEntity> userOpt = userRepository.findById(userId.longValue());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + userId + " not found");
        }

        UserModel userModel = modelMapper.map(userOpt.get(), UserModel.class);
        userModel.setPassword(null);

        return ResponseEntity.ok(userModel);
    }


    @Override
    public ResponseEntity<?> updateUser(UserModel userModel) {
        if (userModel.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User ID must be provided for update");
        }

        // Find existing user by ID
        Optional<UserDetailsEntity> userOpt = userRepository.findById(userModel.getUserId());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + userModel.getUserId() + " not found");
        }

        UserDetailsEntity existingUser = userOpt.get();
        if (userModel.getUserName() != null) existingUser.setUserName(userModel.getUserName());
        if (userModel.getEmail() != null) existingUser.setEmail(userModel.getEmail());
        if (userModel.getPassword() != null) {
            existingUser.setPassword(passwordEncryption.passwordEncoder().encode(userModel.getPassword()));
        }
        if (userModel.getFirstName() != null) existingUser.setFirstName(userModel.getFirstName());
        if (userModel.getLastName() != null) existingUser.setLastName(userModel.getLastName());
        if (userModel.getMobileNumber() != null) existingUser.setMobileNumber(userModel.getMobileNumber());
        if (userModel.getActive() != null) existingUser.setActive(userModel.getActive());

        // Save the updated entity
        userRepository.save(existingUser);

        return ResponseEntity.ok("User with ID " + userModel.getUserId() + " updated successfully");
    }


    @Override
    public ResponseEntity<?> deleteUser(Long userId) {
        Optional<UserDetailsEntity> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with ID " + userId + " not found");
        }
        userRepository.delete(userOpt.get());
        return ResponseEntity.ok("User with ID " + userId + " deleted successfully");
    }


    @Override
    public ResponseEntity<?> validateUser(LoginModel loginModel) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginModel.getUserName(),
                        loginModel.getPassword()
                )
        );

        String token =
                tokenGenerationUtil.generateToken(loginModel.getUserName());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @Override
    public ResponseEntity<?> createOtp(String email) {

        if (email == null || email.isEmpty() || !email.endsWith("@gmail.com")) {
            return ResponseEntity.badRequest()
                    .body("email is mandatory with @gmail.com");
        }

        try {
            int otpNumber = secureRandom.nextInt(100000, 999999);
            String otp = String.valueOf(otpNumber);

            OtpDetailsEntity otpDetailsEntity = new OtpDetailsEntity();
            otpDetailsEntity.setEmail(email);
            otpDetailsEntity.setOtp(otp);
            otpDetailsEntity.setExpiryDate(LocalDateTime.now().plusMinutes(5));
            otpRepository.save(otpDetailsEntity);
            otpProducer.sendOtp(email, otp);

            return ResponseEntity.ok("OTP generated and sent successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error generating OTP");
        }
    }

    @Override
    public ResponseEntity<?> verifyOtp(OtpVerificationModel otpVerificationModel) {

        OtpDetailsEntity otpDetailsEntity = otpRepository.findByEmail(otpVerificationModel.getEmail());

        if (otpDetailsEntity == null) {
            return ResponseEntity.badRequest().body("OTP not found for this mobile number");
        }

        // Check expiry
        if (otpDetailsEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP expired");
        }

        // Validate OTP
        if (!otpDetailsEntity.getOtp().equals(otpVerificationModel.getOtp())) {
            return ResponseEntity.badRequest().body("Incorrect OTP provided");
        }

        Optional<UserDetailsEntity> userDetails = userRepository.findByEmail(otpVerificationModel.getEmail());

        if (userDetails.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Generate token
        String token = tokenGenerationUtil.generateToken(userDetails.get().getUserName());

        // deleting OTP after successful verification
        otpRepository.delete(otpDetailsEntity);

        return ResponseEntity.ok(token);
    }


}
