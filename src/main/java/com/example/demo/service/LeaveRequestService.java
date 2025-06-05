package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.LeaveRequest;
import com.example.demo.enums.LeaveRequestStatus;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.*;
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

    //lấy ds đơn theo ngày và chưa duyệt (status = PENDING, date = createAt)
    public List<LeaveRequest> getTodayPendingLeaveRequests(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();                 // 00:00
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);           // 23:59:59.999...
        return leaveRequestRepository.findAllByStatusAndCreatedAtBetween(
                LeaveRequestStatus.PENDING, startOfDay, endOfDay
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
    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest, Integer employeeId) {
        // kiem tra employeeId co ton tai hay khong, roi gán id cho trường employee trong entity
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        leaveRequest.setEmployee(employee);
        // Tính toán dayCount
        LocalDate start = leaveRequest.getStartDate();
        LocalDate end = leaveRequest.getEndDate();
//        int dayCount = (int) ChronoUnit.DAYS.between(start, end) + 1;
        int dayCount = 0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                dayCount++;
            }
        }
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

        leaveRequest.setStatus(LeaveRequestStatus.PENDING);

        return leaveRequestRepository.save(leaveRequest);
    }

    // Admin duyệt đơn
    public LeaveRequest approveLeaveRequest(Integer id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ"));

        if (request.getStatus() == null || request.getStatus() != LeaveRequestStatus.PENDING) {
            throw new RuntimeException("Không có đơn (id) hoặc trạng thái (status) không phải là PENDING");
        }

        request.setStatus(LeaveRequestStatus.APPROVED);
        return leaveRequestRepository.save(request);
    }

    // Admin từ chối đơn
    public LeaveRequest rejectLeaveRequest(Integer id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nghỉ"));

        if (request.getStatus() == null || request.getStatus() != LeaveRequestStatus.PENDING) {
            throw new RuntimeException("Không có đơn (id) hoặc trạng thái (status) không phải là PENDING");
        }

        request.setStatus(LeaveRequestStatus.REJECTED);
        return leaveRequestRepository.save(request);
    }
    // Lấy paidLeaveDays đe dùng trong Payroll
    public int getPaidLeaveDaysForEmployeeInMonth(int employeeId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return leaveRequestRepository
                .sumPaidLeaveDaysByEmployeeIdAndDateRange(employeeId, start, end, LeaveRequestStatus.APPROVED);
    }
    // Lấy unpaidLeaveDays đe dùng trong Payroll
    public int getUnpaidLeaveDaysForEmployeeInMonth(int employeeId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return leaveRequestRepository
                .sumUnpaidLeaveDaysByEmployeeIdAndDateRange(employeeId, start, end, LeaveRequestStatus.APPROVED);
    }

}


