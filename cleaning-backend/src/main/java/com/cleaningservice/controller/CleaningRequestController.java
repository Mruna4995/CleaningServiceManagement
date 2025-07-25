package com.cleaningservice.controller;

import com.cleaningservice.model.CleaningRequest;
import com.cleaningservice.service.CleaningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cleaning")
public class CleaningRequestController {

    @Autowired
    private CleaningService cleaningService;

    @GetMapping("/all")
    public ResponseEntity<List<CleaningRequest>> getAllRequests() {
        return ResponseEntity.ok(cleaningService.getAllRequests());
    }

    @PostMapping("/request")
    public ResponseEntity<CleaningRequest> createRequest(@RequestBody CleaningRequest request) {
        return ResponseEntity.ok(cleaningService.createRequest(request));
    }
    @PutMapping("/complete/{id}")
    public ResponseEntity<String> completeTask(@PathVariable Long id) {
        cleaningService.markTaskCompleted(id);
        return ResponseEntity.ok("Task marked as completed and user notified.");
    }
}
