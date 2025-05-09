package com.example.demo.service;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Employee;
import com.example.demo.enums.AttendanceStatus;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    //inject
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    // Lấy lịch sử chấm công của nhân viên
    public List<Attendance> getAttendanceHistory(Integer employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    // Lấy cứng danh sách attendance theo ngày hiện tại (có lẽ không dùng nữa)
    public List<Attendance> getTodayAttendance() {
        LocalDate today = LocalDate.now();
        List<Employee> employees = employeeRepository.findAll();
        // tạo ds attendances
        List<Attendance> attendances = new ArrayList<>();

        for (Employee employee : employees) {
            // select ra các bản ghi đã có
            Optional<Attendance> attendanceOpt =
                    attendanceRepository.findByEmployeeIdAndDate(employee.getId(), today);
            // dùng orElseGet để tạo bản ghi mới attendance nếu không có trong db
            // các trường không được set giá trị như checkIn checkOut sẽ mặc định là null
            Attendance attendance = attendanceOpt.orElseGet(() -> {
                Attendance emptyRecord = new Attendance();
                emptyRecord.setEmployee(employee);
                emptyRecord.setDate(today);
                return emptyRecord;
            });

            attendances.add(attendance);
        }

        return attendances;
    }

    public List<Attendance> getAttendanceByDate (LocalDate selectedDate) {
        List<Employee> employees = employeeRepository.findAll();
        List<Attendance> attendances = new ArrayList<>();

        for (Employee employee : employees) {
            Optional<Attendance> attendanceOpt =
                    attendanceRepository.findByEmployeeIdAndDate(employee.getId(), selectedDate);

            Attendance attendance = attendanceOpt.orElseGet(() -> {
                Attendance emptyRecord = new Attendance();
                emptyRecord.setEmployee(employee);
                emptyRecord.setDate(selectedDate);
                return emptyRecord;
            });

            attendances.add(attendance);
        }

        return attendances;
    }
}