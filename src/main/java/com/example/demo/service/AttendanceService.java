package com.example.demo.service;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Employee;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
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

    // logic tinh CHUYEN CAN, dieu kien dat chuyen can la
    public boolean isChuyenCan(int employeeId, int month, int year) {
        // Kiểm tra so ngay` co trong thang: 28 hay 29 hay 30 hay 31 ngay
        YearMonth yearMonth = YearMonth.of(year, month);
        int totalDays = yearMonth.lengthOfMonth();
        //Duyệt tung ngay trong 1 thang
        for (int day = 1; day <= totalDays; day++) {
            // Lấy ra thứ trong tuần của ngày đó (ví dụ: MONDAY, TUESDAY,...)
            LocalDate date = LocalDate.of(year, month, day);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            // Bỏ qua nếu là thứ 7 hoặc CN, kh tính la ngay lam viec
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                continue;
            }
            // Lấy attendance trong ngày đó
            Optional<Attendance> attendanceOpt =
                    attendanceRepository.findByEmployeeIdAndDate(employeeId, date);
            // Không có dữ liệu, hoặc không đủ checkin/checkout → mất chuyên cần
            if (attendanceOpt.isEmpty()
                    || attendanceOpt.get().getCheckInTime() == null
                    || attendanceOpt.get().getCheckOutTime() == null) {
                return false;
            }
        }
        // Không có ngày nào thiếu → đạt chuyên cần
        return true;
    }
    // logic tinh TONG NGAY CONG
    public int calculateWorkingDays(int employeeId, int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        int workingDays = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // Nếu muốn bỏ qua thứ 7 & CN
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
            Optional<Attendance> attendanceOpt = attendanceRepository.findByEmployeeIdAndDate(employeeId, date);
            if (attendanceOpt.isPresent()) {
                Attendance attendance = attendanceOpt.get();
                if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null
                ) {
                    workingDays++; // có đủ công
                }
            }
            // nếu không có bản ghi → bỏ qua ở đây, sẽ dùng ở bước trừ công nếu cần
        }
        return workingDays;
    }



}