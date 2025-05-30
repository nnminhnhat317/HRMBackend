package com.example.demo.service;

import com.example.demo.entity.SalaryLevel;
import com.example.demo.repository.SalaryLevelRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalaryLevelService {
    private final SalaryLevelRepository salaryLevelRepository;
    public SalaryLevelService(SalaryLevelRepository salaryLevelRepository) {
        this.salaryLevelRepository = salaryLevelRepository;
    }
    // lấy ds bậc lương nhân viên dựa theo ngày hiệu lực mới nhất
    public List<SalaryLevel> getLatestSalaryLevelPerEmployee() {
        //dựa vào repo sắp xếp startDate lên đầu tiên => chắc chắn chỉ thêm startDate mới nhất
        List<SalaryLevel> all = salaryLevelRepository.findAllByOrderByEmployeeIdAscStartDateDesc();
        Map<Integer, SalaryLevel> latestPerEmployee = new LinkedHashMap<>();
        for (SalaryLevel level : all) {// duyet tung phan tu
            Integer employeeId = level.getEmployee().getId();// lay ra empId
            if (!latestPerEmployee.containsKey(employeeId)) {// kiem tra empId có trong mảng chưa
                latestPerEmployee.put(employeeId, level);// nếu chưa có thì thêm
            }
        }
        return new ArrayList<>(latestPerEmployee.values());//trả về ds mới nhât
    }

    //ham tu dong cap nhat promote thanh true: chi lay promote false va saveAll de UPDATE vao sql 1 lan
    @Scheduled(cron = "0 49 22 * * *")
    public void updatePromotions() {
        // lay ds Chưa được thăng chức (promote = false)
        //Và ngày bắt đầu hiệu lực (startDate) đã trước ít nhất 1 năm tính từ thời điểm hiện tại.
        // VD hôm nay 29/5/25 thì ngày hiệu lực phải là ngày 28/5/25 để promote set TRUE
        List<SalaryLevel> listCanPromote = salaryLevelRepository
                .findByPromoteFalseAndStartDateBefore(LocalDate.now().minusYears(1));
        for (SalaryLevel promote : listCanPromote) {
            promote.setPromote(true);
        }
        salaryLevelRepository.saveAll(listCanPromote);
    }
    // lay lich su salarylevel cua 1 nhan vien
    public List<SalaryLevel> getSalaryHistoryByEmployeeId(Long employeeId) {
        return salaryLevelRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
    }
}
