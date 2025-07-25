package com.cleaningservice.controller;

import com.cleaningservice.dto.LoginRequest;
import com.cleaningservice.dto.LoginResponse;
import com.cleaningservice.model.CleaningRequest;
import com.cleaningservice.model.User;
import com.cleaningservice.repository.CleaningRequestRepository;
import com.cleaningservice.repository.UserRepository;
import com.cleaningservice.service.EmailService;
import com.cleaningservice.service.JwtService;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CleaningRequestRepository requestRepo;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // In-memory OTP store (for development only)
    private final Map<String, String> otpMap = new HashMap<>();

    /**
     * ✅ 1. Admin Login
     */
    @RestController
    @RequestMapping("/admin")
    public class AdminAuthController {


@PostMapping("/admin/login")
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    String rawPassword = loginRequest.getPassword();
    String storedHashedPassword = "$2a$10$sSEAjK4g1pZL/Ca9fE.lBu1G9YcM2auFZaxJZJwqUlA4MCyMcxHHu"; // from DB

    if ("admin@gmail.com".equals(loginRequest.getEmail()) &&
        passwordEncoder.matches(rawPassword, storedHashedPassword)) {
        return ResponseEntity.ok("Admin login successful");
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}

    /**
     * ✅ 2. Get all cleaning requests
     */
    @GetMapping("/requests")
    public ResponseEntity<List<CleaningRequest>> getAllRequests() {
        return ResponseEntity.ok(requestRepo.findAll());
    }

    /**
     * ✅ 3. Send OTP
     */
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        String otp = emailService.generateOtp();
        otpMap.put(email, otp);

        try {
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok("OTP sent to " + email);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending OTP: " + e.getMessage());
        }
    }

    /**
     * ✅ 4. Verify OTP
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        String storedOtp = otpMap.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpMap.remove(email);
            return ResponseEntity.ok("OTP verified successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
    }

    /**
     * ✅ 5. Assign a request to a staff (User with STAFF role)
     */
    @PostMapping("/assign/{requestId}")
    public ResponseEntity<String> assignRequestToStaff(
            @PathVariable Long requestId,
            @RequestParam Long staffId
    ) {
        Optional<CleaningRequest> requestOpt = requestRepo.findById(requestId);
        Optional<User> staffOpt = userRepo.findById(staffId);

        if (requestOpt.isEmpty() || staffOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request or Staff not found");
        }

        CleaningRequest request = requestOpt.get();
        User staff = staffOpt.get();

        request.setStaff(staff);
        request.setStatus("Assigned");

        requestRepo.save(request);

        return ResponseEntity.ok("Request ID " + requestId + " assigned to staff: " + staff.getName());
    }
}
}
