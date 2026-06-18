package com.expensewise.controller;

import com.expensewise.dto.request.LoginRequest;
import com.expensewise.dto.request.RegisterRequest;
import com.expensewise.dto.response.ApiResponse;
import com.expensewise.dto.response.AuthResponse;
import com.expensewise.model.User;
import com.expensewise.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        User user = authService.getProfile(userId);
        // Mask password before returning
        user.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            HttpServletRequest request,
            @RequestBody Map<String, Object> body) {
        String userId = (String) request.getAttribute("userId");
        String name = (String) body.get("name");
        String currency = (String) body.get("currency");
        double goal = body.containsKey("monthlyIncomeGoal")
                ? ((Number) body.get("monthlyIncomeGoal")).doubleValue() : 0;
        User updated = authService.updateProfile(userId, name, currency, goal);
        updated.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", updated));
    }
}
