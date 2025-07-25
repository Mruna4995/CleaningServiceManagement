package com.cleaningservice.controller;

import com.cleaningservice.model.CleaningRequest;
import com.cleaningservice.service.CleaningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasRole('STAFF')")  // Moved here correctly
public class StaffController {

    @Autowired
    private CleaningService cleaningService;

    @GetMapping("/assigned")
    public List<CleaningRequest> viewAssignedRequests(@RequestParam Long staffId) {
        return cleaningService.getRequestsAssignedToStaff(staffId); // This method you’ll write in service
    }
}
