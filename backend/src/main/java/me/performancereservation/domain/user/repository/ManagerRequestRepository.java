package me.performancereservation.domain.user.repository;

import me.performancereservation.domain.user.entitiy.ManagerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRequestRepository extends JpaRepository<ManagerRequest, Long> {
}
