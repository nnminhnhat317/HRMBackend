package com.example.demo.controller;
import com.example.demo.entity.Attendance;
import com.example.demo.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
// 3 import xử lý phần catch của getAttendanceByDate
import java.time.format.DateTimeParseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/attendances")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {
    private final AttendanceService attendanceService;
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
    // có lẽ không dùng api này
    @GetMapping("/list1")
    public ResponseEntity<List<Attendance>> getTodayAttendance() {
        List<Attendance> attendanceListToday = attendanceService.getTodayAttendance();
        return ResponseEntity.ok(attendanceListToday);
    }

    //Attendance
    @GetMapping("/list")
    public List<Attendance> getAttendanceByDate(@RequestParam(required = false) String date) {
        LocalDate targetDate;
        try {
            targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date format. Expected yyyy-MM-dd"
            );
        }

        return attendanceService.getAttendanceByDate(targetDate);
    }

    //AttendanceDetail
    @GetMapping("/detail")
    public ResponseEntity<List<Attendance>> getAttendanceByEmployeeAndMonth(
            @RequestParam Integer employeeId,
            @RequestParam int month,
            @RequestParam int year
    ) {
        List<Attendance> attendances = attendanceService.getAttendanceByEmployeeAndMonth(employeeId, month, year);
        return ResponseEntity.ok(attendances);
    }
    //AttendanceDetail CHUYEN CAN
    @GetMapping("/isChuyenCan")
    public ResponseEntity<Boolean> isChuyenCan(
            @RequestParam Integer employeeId,
            @RequestParam int month,
            @RequestParam int year
    ) {
        boolean result = attendanceService.isChuyenCan(employeeId, month, year);
        return ResponseEntity.ok(result);
    }
    //AttendanceDetail TONG NGAY CONG
    @GetMapping("/working-days")
    public ResponseEntity<Integer> getWorkingDays(
            @RequestParam Integer employeeId,
            @RequestParam int month,
            @RequestParam int year
    ) {
        int workingDays = attendanceService.calculateWorkingDays(employeeId, month, year);
        return ResponseEntity.ok(workingDays);
    }

}
