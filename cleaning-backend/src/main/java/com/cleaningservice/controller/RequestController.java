package com.cleaningservice.controller;

import com.cleaningservice.model.Request;
import com.cleaningservice.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private JavaMailSender mailSender;
    @GetMapping("/pending")
    public ResponseEntity<List<Request>> getPendingRequests() {
        List<Request> pendingRequests = requestRepository.findByStatus("PENDING");
        return ResponseEntity.ok(pendingRequests);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignStaff(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String assignedTo = body.get("assignedTo");
        String staffEmail = body.get("email"); // 👈 Get staff email

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setAssignedTo(assignedTo);
        requestRepository.save(request);

        sendAssignmentEmail(staffEmail, assignedTo, request); // ✅ Notify staff

        Map<String, String> response = new HashMap<>();
        response.put("message", "Staff assigned successfully!");
        return ResponseEntity.ok(response);

    }

    // ✅ Existing POST endpoint
    @PostMapping
    public ResponseEntity<Map<String, String>> submitRequest(@RequestBody Request request) {
    	 request.setStatus("PENDING");
        requestRepository.save(request);
        sendEmail(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Request submitted successfully!");
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);
        requestRepository.save(request);

        return ResponseEntity.ok("Status updated");
        
    }
    @GetMapping
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("APPROVED");
        requestRepository.save(request);

        sendApprovalEmail(request); // ✅ Notify user

        return ResponseEntity.ok("Request approved");
    }

    // ✅ Reject Request + Send Email
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("REJECTED");
        requestRepository.save(request);

        sendRejectionEmail(request); // ✅ Notify user

        return ResponseEntity.ok("Request rejected");
    }


    // ✅ NEW: GET endpoint to fetch all requests
    

    // ✅ Email notification
    private void sendEmail(Request request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("admin@example.com");
        message.setSubject("New Cleaning Request from " + request.getName());
        message.setText(
            "Name: " + request.getName() + "\n" +
            "Email: " + request.getEmail() + "\n" +
            "Phone: " + request.getPhone() + "\n" +
            "Room No: " + request.getRoomNo() + "\n" +
            "Address: " + request.getAddress() + "\n" +
            "Map URL: " + request.getLocationUrl() + "\n" +
            "Service: " + request.getServiceType()
        );
        mailSender.send(message);
    }
    private void sendApprovalEmail(Request request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("Cleaning Request Approved ✅");
        message.setText("Dear " + request.getName() + ",\n\n" +
            "Your cleaning request for room " + request.getRoomNo() + " has been *approved*.\n\n" +
            "Regards,\nCleaning Service Team");
        mailSender.send(message);
    }

    // ✅ Email to user on rejection
    private void sendRejectionEmail(Request request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("Cleaning Request Rejected ❌");
        message.setText("Dear " + request.getName() + ",\n\n" +
            "Unfortunately, your cleaning request for room " + request.getRoomNo() + " has been *rejected*.\n\n" +
            "If you have questions, please contact admin.\n\n" +
            "Regards,\nCleaning Service Team");
        mailSender.send(message);
    }
    private void sendAssignmentEmail(String email, String staffName, Request request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); // send to staff
        message.setSubject("🧹 New Cleaning Assignment");
        message.setText(
            "Hello " + staffName + ",\n\n" +
            "You have been assigned a new cleaning request:\n\n" +
            "Room No: " + request.getRoomNo() + "\n" +
            "Service Type: " + request.getServiceType() + "\n" +
            "Customer: " + request.getName() + "\n" +
            "Contact: " + request.getPhone() + "\n\n" +
            "Please take action as soon as possible.\n\n" +
            "Best regards,\nCleaning Service Team"
        );

        mailSender.send(message);
    }


}
