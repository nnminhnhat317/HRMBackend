package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String gender;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    @ManyToOne
    @JoinColumn(name="department_id")
    private Department departmentId;
    private String position;
    private Date hireDate;
}
