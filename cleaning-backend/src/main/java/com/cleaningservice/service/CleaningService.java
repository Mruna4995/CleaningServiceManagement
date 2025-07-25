package com.cleaningservice.service;

import java.time.LocalDateTime;
import java.util.List;

import com.cleaningservice.model.User;
import com.cleaningservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cleaningservice.model.CleaningRequest;
import com.cleaningservice.repository.CleaningRequestRepository;

@Service
public class CleaningService {

    @Autowired
    private CleaningRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public CleaningRequest createRequest(CleaningRequest request) {
        request.setStatus("pending");
        request.setCreatedTime(LocalDateTime.now());
        CleaningRequest saved = requestRepository.save(request);

        if (saved.getUser() != null && saved.getUser().getEmail() != null) {
            emailService.sendEmail(
                saved.getUser().getEmail(),
                "Cleaning Request Submitted",
                "Hi " + saved.getUser().getName() + ",\n\nYour cleaning request has been received and is pending approval."
            );
        }

        return saved;
    }

    public List<CleaningRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    public List<CleaningRequest> getRequestsAssignedToStaff(Long staffId) {
        return requestRepository.findByStaff_Id(staffId);
    }
    public List<CleaningRequest> getRequestsByStatus(String status) {
        return requestRepository.findByStatusIgnoreCase(status);
    }


    public void assignRequestToStaff(Long requestId, Long staffId) {
        CleaningRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        request.setStaff(staff); // Ensure your entity has this field
        request.setStatus("assigned");
        requestRepository.save(request);

        emailService.sendEmail(
            staff.getEmail(),
            "New Cleaning Task Assigned",
            "Hi " + staff.getName() + ",\n\nYou have been assigned a new cleaning task. Please check your dashboard."
        );
    }

    // ✅ Add this method here
    public void markTaskCompleted(Long requestId) {
        CleaningRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("completed");
        request.setCompletedTime(LocalDateTime.now()); // Ensure this field exists in the entity
        requestRepository.save(request);

        // Email to user
        emailService.sendEmail(
            request.getUser().getEmail(),
            "Cleaning Completed",
            "Hi " + request.getUser().getName() + ",\n\nYour cleaning request has been successfully completed!"
        );
    }
    public void rateCleaning(Long requestId, int rating, String feedback) {
        CleaningRequest request = requestRepository.findById(requestId).orElseThrow();
        request.setRating(rating);
        request.setFeedback(feedback);
        requestRepository.save(request);
    
	}

    public void updateStatus(Long requestId, String status) {
        CleaningRequest request = requestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(status.toLowerCase()); // Save in lowercase
        requestRepository.save(request);

        // Optional: Send email if needed
        if (status.equalsIgnoreCase("approved")) {
            emailService.sendEmail(
                request.getUser().getEmail(),
                "Request Approved",
                "Hi " + request.getUser().getName() + ",\n\nYour cleaning request has been approved!"
            );
        } else if (status.equalsIgnoreCase("rejected")) {
            emailService.sendEmail(
                request.getUser().getEmail(),
                "Request Rejected",
                "Hi " + request.getUser().getName() + ",\n\nSorry, your cleaning request has been rejected."
            );
        }
    }


}
