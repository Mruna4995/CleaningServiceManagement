package com.cleaningservice.dto;

public class LoginResponse {
    private String token;
    private String email;
    private String name;
    private String role;
    private String joinedDate;
    private boolean assigned;

    public LoginResponse(String token, String email, String name, String role, String joinedDate, boolean assigned) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
        this.joinedDate = joinedDate;
        this.assigned = assigned;
    }

    // ✅ Getters
    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public boolean isAssigned() {
        return assigned;
    }

    // ❓ Optional: Setters (if needed by Jackson or Angular)
    public void setToken(String token) {
        this.token = token;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
}
