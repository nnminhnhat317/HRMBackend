package com.example.demo.entity;

import com.example.demo.enums.LeaveRequestStatus;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "leave_request")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer dayCount;
    private Integer paidLeaveDays;
    private Integer unpaidLeaveDays;
    private String reason;
    private String leaveType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveRequestStatus status;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
