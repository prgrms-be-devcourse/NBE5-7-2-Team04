package me.performancereservation.domain.performance.repository;

import me.performancereservation.domain.performance.entities.PerformanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {
    // 공연 아이디로 모든 회차 가져오기
    List<PerformanceSchedule> findByPerformanceId(Long id);
}