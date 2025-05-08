package me.performancereservation.domain.performance.repository;

import me.performancereservation.domain.performance.entities.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
}
