package com.example.demo.service;

import com.example.demo.entity.LeaveRequest;
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
    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }
    //lấy ds đơn ngày hôm nay chưa duyệt (status = PENDING, date = createAt)
    public List<LeaveRequest> getTodayPendingLeaveRequests(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();                 // 00:00
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);           // 23:59:59.999...
        return leaveRequestRepository.findAllByStatusAndCreatedAtBetween(
                "PENDING", startOfDay, endOfDay
        );
    }
    // Lấy ngày nghỉ phép còn lại remaining trong 1 năm
    public Integer getRemainingPaidLeave(Integer employeeId, int year) {
        // Tính tổng ngày nghỉ phép đã dùng used trong 1 năm
        Integer used = leaveRequestRepository.sumPaidLeaveDaysInYear(employeeId, year);
        return 20 - (used != null ? used : 0);
    }
    // Khi tạo đơn sẽ tính  toán day_count, paid_leave_days, unpaid_leave_days
    // Chức năng cho user tạo đơn
}


