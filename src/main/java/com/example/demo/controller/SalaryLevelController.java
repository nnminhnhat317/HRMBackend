package com.example.demo.controller;

import com.example.demo.entity.SalaryLevel;
import com.example.demo.service.SalaryLevelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary-level")
@CrossOrigin(origins = "http://localhost:5173")
public class SalaryLevelController {
    private final SalaryLevelService salaryLevelService;

    public SalaryLevelController(SalaryLevelService salaryLevelService) {
        this.salaryLevelService = salaryLevelService;
    }

    @GetMapping("/latest")
    public ResponseEntity<List<SalaryLevel>> getLatestSalaryLevels() {
        List<SalaryLevel> result = salaryLevelService.getLatestSalaryLevelPerEmployee();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{employeeId}")
    public List<SalaryLevel> getSalaryHistoryByEmployeeId(@PathVariable Long employeeId) {
        return salaryLevelService.getSalaryHistoryByEmployeeId(employeeId);
    }
}
