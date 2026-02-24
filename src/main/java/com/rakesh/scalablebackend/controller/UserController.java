package com.rakesh.scalablebackend.controller;

import com.rakesh.scalablebackend.dto.ApiResponse;
import com.rakesh.scalablebackend.dto.LoginRequest;
import com.rakesh.scalablebackend.dto.UserResponse;
import com.rakesh.scalablebackend.entity.User;
import com.rakesh.scalablebackend.security.JwtService;
import com.rakesh.scalablebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService,
                          JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Register
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody User user) {

        UserResponse response = userService.register(user);

        return ResponseEntity.ok(
                new ApiResponse<>(true, response, null)
        );
    }

    // Authenticated users
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        return ResponseEntity.ok(
                new ApiResponse<>(true, userService.getAllUsers(), null)
        );
    }

    // ADMIN only
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsersForAdmin() {

        return ResponseEntity.ok(
                new ApiResponse<>(true, userService.getAllUsers(), null)
        );
    }

    // Current logged-in user
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<String>> me() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, null, "Unauthorized"));
        }

        String email = authentication.getName();

        return ResponseEntity.ok(
                new ApiResponse<>(true, email, null)
        );
    }

    // Refresh Access Token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(
            @RequestBody Map<String, String> request) {

        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || !jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, null, "Invalid refresh token"));
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(false, null, "User not found"));
        }

        if (!refreshToken.equals(user.getRefreshToken())) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, null, "Refresh token mismatch"));
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        Map.of("accessToken", newAccessToken),
                        null
                )
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userService.findByEmail(email);

        if (user != null) {
            user.setRefreshToken(null);
            userService.save(user);
        }

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Logged out successfully", null)
        );
    }
    // Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @RequestBody LoginRequest request) {

        User user = userService.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(false, null, "User not found"));
        }

        if (!userService.passwordMatches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, null, "Invalid password"));
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

// STORE refresh token in DB
        user.setRefreshToken(refreshToken);
        userService.save(user);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        Map.of(
                                "accessToken", accessToken,
                                "refreshToken", refreshToken
                        ),
                        null
                )
        );
    }
}
