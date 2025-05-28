package com.example.demo.controller;

import com.example.demo.entity.LeaveRequest;
import com.example.demo.service.LeaveRequestService;
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

    // lấy số ngày nghỉ phép còn lại trong năm
    // dùng để Hiển thị trước khi tạo đơn trả về số ngày nghỉ phép còn lại trong năm của nhân viên
    // dùng để Ẩn/disable option 'Nghỉ phép năm' nếu hết phép
    // dùng để Cảnh báo khi người dùng nhập đơn nhiều hơn số ngày còn lại:
    //ví dụ: "Bạn chỉ còn 2 ngày nghỉ phép. Đơn này sẽ tính 1 ngày không phép."

    @GetMapping("/remaining/{employeeId}")
    public ResponseEntity<Integer> getRemainingPaidLeave(@PathVariable Integer employeeId) {
        int remaining = leaveRequestService.getRemainingPaidLeave(employeeId, Year.now().getValue());
        return ResponseEntity.ok(remaining);
    }
}
