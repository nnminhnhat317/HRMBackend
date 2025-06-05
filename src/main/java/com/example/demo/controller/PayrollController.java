package com.example.demo.controller;

import com.example.demo.entity.Payroll;
import com.example.demo.service.PayrollService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payroll")
@CrossOrigin(origins = "http://localhost:5173")
public class PayrollController {
    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generatePayroll(
            @RequestParam int month,
            @RequestParam int year
    ) {
        payrollService.generatePayroll(month, year);
        return ResponseEntity.ok("Payroll generated for " + month + "/" + year);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Payroll>> getPayrolls(
            @RequestParam int month,
            @RequestParam int year) {
        List<Payroll> payrolls = payrollService.getPayrollsByMonthAndYear(month, year);
        return ResponseEntity.ok(payrolls);
    }
}
