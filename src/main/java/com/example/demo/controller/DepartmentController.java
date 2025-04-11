package com.example.demo.controller;

import com.example.demo.entity.Department;
import com.example.demo.entity.Employee;
import com.example.demo.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/departments")
@CrossOrigin(origins = "http://localhost:5173")
public class DepartmentController {
    //tiêm service vào controller thông qua constructor
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    private final DepartmentService departmentService;
    @GetMapping("/list")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }
}
