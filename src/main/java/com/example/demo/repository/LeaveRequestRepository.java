package com.example.demo.repository;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findAllByStatusAndCreatedAtBetween(
            String status, LocalDateTime startOfDay, LocalDateTime endOfDay
    );
}

