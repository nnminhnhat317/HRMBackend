package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.entity.Payroll;
import com.example.demo.enums.LeaveRequestStatus;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.LeaveRequestRepository;
import com.example.demo.repository.PayrollRepository;
import com.example.demo.repository.SalaryLevelRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollService {
    //inject
    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    // su dung service cua cac nghiep vu khac
    private final AttendanceService attendanceService;
    private final SalaryLevelService salaryLevelService;
    private final LeaveRequestService leaveRequestService;

    public PayrollService(PayrollRepository payrollRepository,
                          EmployeeRepository employeeRepository,
                          AttendanceService attendanceService,
                          SalaryLevelService salaryLevelService,
                          LeaveRequestService leaveRequestService) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceService = attendanceService;
        this.salaryLevelService = salaryLevelService;
        this.leaveRequestService = leaveRequestService;
    }

    // tao bang luong
    public void generatePayroll(int month, int year) {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {
            // Kiểm tra nếu đã có payroll trong tháng thi bo qua
            Optional<Payroll> existing = payrollRepository
                    .findByEmployeeAndMonthAndYear(employee, month, year);
            if (existing.isPresent()) continue;

            // 1. Lấy lương cơ bản mới nhất
            Double baseSalary = salaryLevelService
                    .getBaseSalaryByEmployeeId(employee.getId());

            // 2. Số ngày làm việc thực tế
            int workingDays = attendanceService
                    .calculateWorkingDays(employee.getId(), month, year);

            // 3. Nghỉ không phép
            //service nay dat nguoc giá trị year và month
            int unpaidLeaveDays = leaveRequestService
                    .getUnpaidLeaveDaysForEmployeeInMonth(employee.getId(), year, month);

            // 4. Phụ cấp (nếu chuyên cần)
            Double allowance = attendanceService
                    .isChuyenCan(employee.getId(), month, year) ? 500_000.0 : 0.0;

            // 5. Khấu trừ = mỗi ngày nghỉ không phép trừ lương theo công chuẩn (22 ngày)
            Double deduction = (baseSalary / 22.0) * unpaidLeaveDays;

            // 6. Lương thực nhận
            Double totalSalary = (baseSalary / 22.0) * workingDays + allowance - deduction;

            // 7. Lưu vào payroll
            Payroll payroll = new Payroll();

            payroll.setEmployee(employee);
            payroll.setMonth(month);
            payroll.setYear(year);
            payroll.setBaseSalary(baseSalary);
            payroll.setWorkingDays(workingDays);
            payroll.setUnpaidLeaveDays(unpaidLeaveDays);
            payroll.setAllowance(allowance);
            payroll.setDeduction(deduction);
            payroll.setTotalSalary(totalSalary);

            payrollRepository.save(payroll);
        }
    }
    // lay danh sach luong
    public List<Payroll> getPayrollsByMonthAndYear(int month, int year) {
        return payrollRepository.findByMonthAndYear(month, year);
    }


}



