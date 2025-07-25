package com.cleaningservice.controller;

import com.cleaningservice.config.JwtTokenProvider;
import com.cleaningservice.dto.LoginRequest;
import com.cleaningservice.dto.LoginResponse;
import com.cleaningservice.dto.RegisterRequest;
import com.cleaningservice.model.User;
import com.cleaningservice.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // 🔁 Allow Angular frontend
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails.getUsername());

            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean assigned = user.getAssignedRequests() != null && !user.getAssignedRequests().isEmpty();

            LoginResponse response = new LoginResponse(
                    token,
                    user.getEmail(),
                    user.getName(),
                    user.getRole(),
                    user.getJoinedDate().toString(),
                    assigned
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("❌ Invalid credentials");
        }
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("❌ Email already registered.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setJoinedDate(LocalDate.now());

        userRepository.save(user);

        return ResponseEntity.ok("✅ User registered successfully.");
    }
}
