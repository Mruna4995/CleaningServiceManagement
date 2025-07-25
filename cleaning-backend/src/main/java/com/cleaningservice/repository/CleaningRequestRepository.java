package com.cleaningservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cleaningservice.model.CleaningRequest;

@Repository
public interface CleaningRequestRepository extends JpaRepository<CleaningRequest, Long> {
	List<CleaningRequest> findByStaff_Id(Long staffId);
	List<CleaningRequest> findByStatusIgnoreCase(String status);


}