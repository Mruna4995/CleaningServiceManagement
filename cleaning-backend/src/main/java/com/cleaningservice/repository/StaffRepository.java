package com.cleaningservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cleaningservice.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    // Optional: custom queries like findByEmail, findByAvailability, etc.
    Staff findByEmail(String email);
}
