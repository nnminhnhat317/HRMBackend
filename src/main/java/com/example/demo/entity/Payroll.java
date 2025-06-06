package com.example.demo.entity;

import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payroll")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private int month;
    private int year;
    private Double baseSalary;
    private int workingDays;
    private int paidLeaveDays;
    private int unpaidLeaveDays;
    private Double allowance;
    private Double deduction;
    private Double totalSalary;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
