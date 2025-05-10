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

    // Lấy lịch sử chấm công của nhân viên (không sử dụng)
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
    //attendance
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

    //attendanceDetail
    public List<Attendance> getAttendanceByEmployeeAndMonth(Integer employeeId, int month, int year) {
        List<Attendance> result = new ArrayList<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Lấy employee 1 lần
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Lấy danh sách chấm công thực tế trong tháng
        List<Attendance> attendanceList = attendanceRepository.findByEmployeeIdAndDateBetween(
                employeeId, startDate, endDate
        );

        Map<LocalDate, Attendance> attendanceMap = attendanceList.stream()
                .collect(Collectors.toMap(Attendance::getDate, a -> a));

        // Duyệt từng ngày trong tháng, điền dữ liệu nếu có, nếu không thì tạo bản ghi rỗng
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Attendance attendance = attendanceMap.getOrDefault(date, createEmptyAttendance(employee, date));
            result.add(attendance);
        }

        return result;
    }
    private Attendance createEmptyAttendance(Employee employee, LocalDate date) {
        Attendance empty = new Attendance();
        empty.setEmployee(employee);
        empty.setDate(date);
        // Có thể set status mặc định như "CHUA_CHAM_CONG"
        return empty;
    }
}