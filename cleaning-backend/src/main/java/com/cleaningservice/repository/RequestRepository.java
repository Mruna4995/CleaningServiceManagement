package com.cleaningservice.repository;

import com.cleaningservice.model.Request;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
	List<Request> findByStatus(String status);

}

