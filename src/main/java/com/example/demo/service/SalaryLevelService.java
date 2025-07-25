package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.enums.PromoteStatus;
import com.example.demo.entity.SalaryLevel;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.SalaryLevelRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class SalaryLevelService {
    private final SalaryLevelRepository salaryLevelRepository;
    private final EmployeeRepository employeeRepository;

    public SalaryLevelService(SalaryLevelRepository salaryLevelRepository,
                              EmployeeRepository employeeRepository) {
        this.salaryLevelRepository = salaryLevelRepository;
        this.employeeRepository = employeeRepository;
    }

    // lấy ds bậc lương nhân viên dựa theo ngày hiệu lực mới nhất
    public List<SalaryLevel> getLatestSalaryLevelPerEmployee() {
        List<Employee> employees = employeeRepository.findAll();
        List<SalaryLevel> result = new ArrayList<>();

        for (Employee employee : employees) {
            // Tìm bậc lương mới nhất của nhân viên này (nếu có)
            Optional<SalaryLevel> optionalLevel  = salaryLevelRepository
                    .findTopByEmployeeIdOrderByStartDateDesc(employee.getId());
            SalaryLevel latest = optionalLevel.orElse(null);
            if (latest != null) {
                result.add(latest);
            } else {
                // Nếu chưa có bậc lương(nhân vien moi them), tạo bản ghi tạm với thông tin nhân viên
                // Để tương tác và chuyển sang SalaryDetail
                SalaryLevel emptyLevel = new SalaryLevel();
                emptyLevel.setEmployee(employee);
                // Các trường khác có thể để null
                result.add(emptyLevel);
            }
        }
        return result;
    }

    // lay lich su salarylevel cua 1 nhan vien
    public List<SalaryLevel> getSalaryHistoryByEmployeeId(Integer employeeId) {
        return salaryLevelRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
    }

    // them bac luong moi
    public SalaryLevel addSalaryLevel(Integer employeeId, SalaryLevel salaryLevel) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Cập nhật bản ghi cũ "ELIGIBLE" thành "PROMOTED"
        List<SalaryLevel> previousLevels = salaryLevelRepository
                .findByEmployeeIdAndPromoteStatus(employeeId, PromoteStatus.ELIGIBLE);
        for (SalaryLevel oldLevel : previousLevels) {
            oldLevel.setPromoteStatus(PromoteStatus.PROMOTED);
        }
        salaryLevelRepository.saveAll(previousLevels);

        // Thêm bản ghi mới với trạng thái mặc định
        salaryLevel.setEmployee(employee);
        salaryLevel.setPromoteStatus(PromoteStatus.NOT_ELIGIBLE);
        return salaryLevelRepository.save(salaryLevel);
    }
    // chạy mỗi ngày lúc 1:00 sáng cập nhật 1 bản ghi mới gần nhất
    @Scheduled(cron = "0 55 13 * * *")
    public void updatePromotionStatus() {
        List<Employee> allEmployees = employeeRepository.findAll();

        for (Employee employee : allEmployees) {
            // Tìm bản ghi gần nhất (mới nhất) theo ngày startDate
            Optional<SalaryLevel> latestSalaryLevelOpt = salaryLevelRepository
                    .findTopByEmployeeIdOrderByStartDateDesc(employee.getId());

            if (latestSalaryLevelOpt.isPresent()) {
                SalaryLevel level = latestSalaryLevelOpt.get();

                // Nếu chưa đủ điều kiện và ngày bắt đầu đã quá 1 năm
                if (level.getPromoteStatus() == PromoteStatus.NOT_ELIGIBLE
                        && level.getStartDate().isBefore(LocalDate.now().minusYears(1))) {

                    level.setPromoteStatus(PromoteStatus.ELIGIBLE);
                    salaryLevelRepository.save(level);
                }
            }
        }
    }
    // lay baseSalary cho PayrollService su dung
    public Double getBaseSalaryByEmployeeId(Integer employeeId) {
        Optional<SalaryLevel> optionalLevel = salaryLevelRepository.findTopByEmployeeIdOrderByStartDateDesc(employeeId);

        return salaryLevelRepository.findTopByEmployeeIdOrderByStartDateDesc(employeeId)
                .map(SalaryLevel::getBaseSalary) // getBaseSalary là Double
                .orElse(0.0); // Hoặc ném lỗi nếu muốn xử lý chặt chẽ // hoặc throw nếu bạn muốn bắt buộc phải có
    }
}
