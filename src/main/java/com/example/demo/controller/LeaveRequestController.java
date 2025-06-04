package com.example.demo.controller;

import com.example.demo.entity.LeaveRequest;
import com.example.demo.service.LeaveRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping(path = "/leave-request")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveRequestController {
    private final LeaveRequestService leaveRequestService;
    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }
    // lấy ds đơn chưa duyệt PENDING theo ngày
    @GetMapping("/list")
    public ResponseEntity<List<LeaveRequest>> getTodayRequests(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<LeaveRequest> todayRequests = leaveRequestService.getTodayPendingLeaveRequests(date);
        return ResponseEntity.ok(todayRequests);
    }

    // lấy số ngày nghỉ phép còn lại trong năm nay
    // dùng để Hiển thị trước khi tạo đơn trả về số ngày nghỉ phép còn lại trong năm của nhân viên
    // dùng để Cảnh báo khi người dùng nhập đơn nhiều hơn số ngày còn lại:
    //ví dụ: "Bạn chỉ còn 2 ngày nghỉ phép. Đơn này sẽ tính 1 ngày không phép."
    @GetMapping("/remaining-days")
    public ResponseEntity<Integer> getRemainingPaidLeave(HttpServletRequest request) {
        Integer employeeId = (Integer) request.getAttribute("employeeId");
        int remaining = leaveRequestService.getRemainingPaidLeave(employeeId, Year.now().getValue());
        return ResponseEntity.ok(remaining);
    }
    // User tạo đơn
    @PostMapping("/create")
    public ResponseEntity<?> createLeaveRequest(HttpServletRequest request,
                                                @RequestBody LeaveRequest leaveRequest) {
        Integer employeeId = (Integer) request.getAttribute("employeeId");
        LeaveRequest created = leaveRequestService.createLeaveRequest(leaveRequest, employeeId);
        return ResponseEntity.ok(created);
    }
    // Admin duệt đơn, chỉ đổi một trường status của đơn nên dùng @Patch để  tối ưu
    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Integer id) {
        LeaveRequest updated = leaveRequestService.approveLeaveRequest(id);
        return ResponseEntity.ok(updated);
    }
    // Admin từ chối đơn
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Integer id) {
        LeaveRequest updated = leaveRequestService.rejectLeaveRequest(id);
        return ResponseEntity.ok(updated);
    }

}
