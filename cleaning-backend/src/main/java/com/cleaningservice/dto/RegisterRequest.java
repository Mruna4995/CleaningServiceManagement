package com.cleaningservice.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role;  // Optional: "USER", "STAFF", etc.
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}
}
