package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository,
                               EmployeeRepository employeeRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
    }
    //lấy ds đơn ngày hôm nay chưa duyệt (status = PENDING, date = createAt)
    public List<LeaveRequest> getTodayPendingLeaveRequests(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();                 // 00:00
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);           // 23:59:59.999...
        return leaveRequestRepository.findAllByStatusAndCreatedAtBetween(
                "PENDING", startOfDay, endOfDay
        );
    }
    // Lấy ngày nghỉ phép còn lại remaining trong 1 năm, mặc định ban dau là 20
    // Điều kiện tính là đơn có status = APPROVED
    public Integer getRemainingPaidLeave(Integer employeeId, int year) {
        // Tính tổng ngày nghỉ phép đã dùng used trong 1 năm
        Integer used = leaveRequestRepository.sumPaidLeaveDaysInYear(employeeId, year);
        return 20 - (used != null ? used : 0);
    }
    // Khi tạo đơn sẽ tính  toán day_count, paid_leave_days, unpaid_leave_days
    // Chức năng cho user tạo đơn
    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest,Integer employeeId) {
        // kiem tra employeeId co ton tai hay khong, roi gán id cho trường employee trong entity
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        leaveRequest.setEmployee(employee);
        // Tính toán dayCount
        LocalDate start = leaveRequest.getStartDate();
        LocalDate end = leaveRequest.getEndDate();
        int dayCount = (int) ChronoUnit.DAYS.between(start, end) + 1;
        leaveRequest.setDayCount(dayCount);

        if ("ANNUAL".equalsIgnoreCase(leaveRequest.getLeaveType())) {
            int remaining = getRemainingPaidLeave(employeeId, Year.now().getValue());
            int paidLeaveDays = Math.min(dayCount, remaining);
            int unpaidLeaveDays = dayCount - paidLeaveDays;

            leaveRequest.setPaidLeaveDays(paidLeaveDays);
            leaveRequest.setUnpaidLeaveDays(unpaidLeaveDays);
        } else {
            // Nếu là UNPAID
            leaveRequest.setPaidLeaveDays(0);
            leaveRequest.setUnpaidLeaveDays(dayCount);
        }

        leaveRequest.setStatus("PENDING");

        return leaveRequestRepository.save(leaveRequest);
    }
}


