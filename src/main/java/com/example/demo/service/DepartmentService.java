package com.example.demo.service;

import com.example.demo.entity.Department;
import com.example.demo.entity.Employee;
import com.example.demo.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    //inject
    private final DepartmentRepository deparmentRepository;
    public DepartmentService(DepartmentRepository employeeRepository) {
        this.deparmentRepository = employeeRepository;
    }

    public List<Department> getAllDepartments() {
        return deparmentRepository.findAll();
    }
}
