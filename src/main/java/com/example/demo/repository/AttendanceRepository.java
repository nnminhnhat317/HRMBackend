package com.example.demo.repository;

import com.example.demo.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    // Tìm tất cả chấm công của một nhân viên
    List<Attendance> findByEmployeeId(Integer employeeId);

    // Tìm chấm công theo ngày và nhân viên (dùng cho check-in, check-out)
    Optional<Attendance> findByEmployeeIdAndDate(Integer employeeId, LocalDate date);

    // Tìm tất cả chấm công trong một ngày
    List<Attendance> findByDate(LocalDate date);


}