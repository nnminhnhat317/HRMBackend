package com.example.demo.entity;

import com.example.demo.enums.PromoteStatus;
import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "salary_level")
public class SalaryLevel {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String levelName;
    private Double baseSalary;
    private LocalDate startDate;
    @Enumerated(EnumType.STRING)
    private PromoteStatus promoteStatus;
    private String note;
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
