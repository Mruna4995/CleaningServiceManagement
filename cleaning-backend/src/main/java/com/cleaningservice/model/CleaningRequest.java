package com.cleaningservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cleaning_requests")
public class CleaningRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String areaType;        // e.g., Room, Kitchen
    private String description;     // Request details
    private String status;          // PENDING, APPROVED, REJECTED, COMPLETED

    private LocalDateTime createdTime;
    private LocalDateTime completedTime;

    private Integer rating;         // 1 to 5
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;              // The person who made the request

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;             // The staff assigned to fulfill the request

    // 🔸 Constructors
    public CleaningRequest() {
        this.createdTime = LocalDateTime.now();
        this.status = "PENDING";
    }

    public CleaningRequest(String areaType, String description, User user) {
        this();
        this.areaType = areaType;
        this.description = description;
        this.user = user;
    }

    // 🔸 Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }
}
