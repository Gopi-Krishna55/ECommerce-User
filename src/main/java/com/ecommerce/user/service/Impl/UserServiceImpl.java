package com.ecommerce.user.service.Impl;

import com.ecommerce.user.config.PasswordEncryption;
import com.ecommerce.user.entity.UserDetailsEntity;
import com.ecommerce.user.model.LoginModel;
import com.ecommerce.user.model.UserModel;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncryption passwordEncryption;
    private final TokenGenerationUtil tokenGenerationUtil;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncryption passwordEncryption,
            TokenGenerationUtil tokenGenerationUtil,
            AuthenticationManager authenticationManager,
            ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncryption = passwordEncryption;
        this.tokenGenerationUtil = tokenGenerationUtil;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
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
}
