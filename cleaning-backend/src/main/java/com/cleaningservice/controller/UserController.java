package com.cleaningservice.controller;

import com.cleaningservice.model.*;
import com.cleaningservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CleaningService cleaningService;


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }
    @PostMapping("/rate")
    public ResponseEntity<String> rateService(@RequestParam Long requestId,
                                              @RequestParam int rating,
                                              @RequestParam(required = false) String feedback) {
        cleaningService.rateCleaning(requestId, rating, feedback);
        return ResponseEntity.ok("Thanks for rating!");
    }

}