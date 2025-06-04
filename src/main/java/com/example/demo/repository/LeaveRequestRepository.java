package com.example.demo.repository;

import com.example.demo.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    //Lấy tất cả đơn nghỉ chưa duyệt (status = PENDING) theo ngày tạo đơn createAt
    List<LeaveRequest> findAllByStatusAndCreatedAtBetween(
            String status, LocalDateTime startOfDay, LocalDateTime endOfDay
    );
    // Truy vấn JPQL Tổng số ngày nghỉ có phép đã sử dụng trong năm voi status APPROVED
    // :employeeId dấu : là biến liên kết với tham số của method là @Param("employeeId")
    @Query("SELECT SUM(lr.paidLeaveDays) FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
            "AND YEAR(lr.startDate) = :year AND lr.status = 'APPROVED'")
    Integer sumPaidLeaveDaysInYear(@Param("employeeId") Integer employeeId, @Param("year") int year);
}

