package com.example.demo.repository;

import com.example.demo.entity.Employee;
import com.example.demo.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Integer> {
    Optional<Payroll> findByEmployeeAndMonthAndYear(Employee employee, int month, int year);
    List<Payroll> findByMonthAndYear(int month, int year);
}
