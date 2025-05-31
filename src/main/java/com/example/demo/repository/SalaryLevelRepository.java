package com.example.demo.repository;

import com.example.demo.entity.SalaryLevel;
import com.example.demo.enums.PromoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryLevelRepository extends JpaRepository<SalaryLevel, Integer> {
    //lay ds theo employeeId tang dan va trong trong nhóm employeeId sap xep theo startDate giam dan
    List<SalaryLevel> findAllByOrderByEmployeeIdAscStartDateDesc();
    //tim danh sách promote la false
    List<SalaryLevel> findByPromoteStatusFalseAndStartDateBefore(LocalDate date);
    //Lay lich su salarylevel cua 1 nhan vien
    List<SalaryLevel> findByEmployeeIdOrderByStartDateDesc(Integer employeeId);
    //cai tien logic
    Optional<SalaryLevel> findTopByEmployeeIdOrderByStartDateDesc(Integer employeeId);

    List<SalaryLevel> findByEmployeeIdAndPromoteStatus(Integer employeeId, PromoteStatus promoteStatus);
}
