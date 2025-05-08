package me.performancereservation.domain.performance.repository;

import me.performancereservation.domain.performance.entities.PerformanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceScheduleRepository extends JpaRepository<PerformanceSchedule, Long> {
} 