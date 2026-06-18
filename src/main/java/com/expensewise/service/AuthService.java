package com.expensewise.service;

import com.expensewise.dto.request.LoginRequest;
import com.expensewise.dto.request.RegisterRequest;
import com.expensewise.dto.response.AuthResponse;
import com.expensewise.exception.DuplicateResourceException;
import com.expensewise.exception.ResourceNotFoundException;
import com.expensewise.model.User;
import com.expensewise.repository.UserRepository;
import com.expensewise.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CategoryService categoryService;

    /**
     * Registers a new user and seeds default categories.
     * Throws DuplicateResourceException if username/email is already taken.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .monthlyIncomeGoal(request.getMonthlyIncomeGoal())
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        // Seed default categories for the new user
        categoryService.seedDefaultCategories(user.getId());

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return buildAuthResponse(token, user);
    }

    /**
     * Authenticates user credentials and returns a JWT.
     */
    public AuthResponse login(LoginRequest request) {
        // Spring Security handles credential validation
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return buildAuthResponse(token, user);
    }

    /** Get user profile by ID (extracted from JWT in controller) */
    public User getProfile(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /** Update editable profile fields: name, currency, monthlyIncomeGoal */
    public User updateProfile(String userId, String name, String currency, double monthlyIncomeGoal) {
        User user = getProfile(userId);
        if (name != null && !name.isBlank()) user.setName(name);
        if (currency != null && !currency.isBlank()) user.setCurrency(currency);
        user.setMonthlyIncomeGoal(monthlyIncomeGoal);
        return userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .currency(user.getCurrency())
                .build();
    }
}
