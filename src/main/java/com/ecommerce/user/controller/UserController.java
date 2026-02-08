package com.ecommerce.user.controller;

import com.ecommerce.user.model.LoginModel;
import com.ecommerce.user.model.UserModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "APIs for User Management")
public interface UserController {

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PostMapping("/register")
    ResponseEntity<?> addUser(
            @RequestBody @Schema(description = "User details") UserModel userModel,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            @Parameter(description = "Bearer token", required = true)
            String authorization
    );



    @Operation(
            summary = "Get all users",
            description = "Fetch all registered users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping("/read")
    ResponseEntity<?> getAllUsers(
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            @Parameter(description = "Bearer token", required = true)
            String authorization
    );

    @Operation(
            summary = "User login",
            description = "Authenticate user using credentials",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping("/login")
    ResponseEntity<?> login(
            @RequestBody @Schema(description = "Login credentials") LoginModel loginDTO
    );

    @Operation(
            summary = "Get user by ID",
            description = "Fetch user details using user ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping("/get")
    ResponseEntity<?> getUserById(
            @RequestParam("userId")
            @Parameter(description = "User ID", required = true)
            Integer userId,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            @Parameter(description = "Bearer token", required = true)
            String authorization
    );

    @Operation(
            summary = "Update user",
            description = "Update existing user details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @PutMapping("/update")
    ResponseEntity<?> updateUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            @Parameter(description = "Bearer token", required = true)
            String authorization,
            @RequestBody @Schema(description = "Updated user details") UserModel userModel
    );

    @Operation(
            summary = "Delete user",
            description = "Delete user by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @DeleteMapping("/delete/{userId}")
    ResponseEntity<?> deleteUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            @Parameter(description = "Bearer token", required = true)
            String authorization,
            @PathVariable("userId")
            @Parameter(description = "User ID", required = true)
            Long userId
    );
}
