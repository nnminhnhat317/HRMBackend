package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    //inject
    private final EmployeeRepository employeeRepository;
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployeeById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Employee ID must not be null");
        }
        return employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Integer id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setDateOfBirth(employeeDetails.getDateOfBirth());
        employee.setGender(employeeDetails.getGender());
        employee.setPhone(employeeDetails.getPhone());
        employee.setEmail(employeeDetails.getEmail());
        employee.setDepartmentId(employeeDetails.getDepartmentId());
        employee.setPosition(employeeDetails.getPosition());
        employee.setHireDate(employeeDetails.getHireDate());
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Integer id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found " + id);
        }
        employeeRepository.deleteById(id);
    }

}
