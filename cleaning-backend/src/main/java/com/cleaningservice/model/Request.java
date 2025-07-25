package com.cleaningservice.model;

import jakarta.persistence.*;

@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String status = "PENDING"; // ✅ Default value
    @Column(name = "assigned_to")
    private String assignedTo;
    @Column
    private String staffEmail;

    private String name;
    private String email;
    private String phone;
    private String roomNo;
    private String address;
    private String locationUrl;
    private String serviceType;
    

    public String getStaffEmail() {
		return staffEmail;
	}

	public void setStaffEmail(String staffEmail) {
		this.staffEmail = staffEmail;
	}

	public String getStatus() {
		return status;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// 👉 Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getAddress() {
        return address;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public String getServiceType() {
        return serviceType;
    }

    // 👉 Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
