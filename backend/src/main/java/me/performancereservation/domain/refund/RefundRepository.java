package me.performancereservation.domain.refund;

import me.performancereservation.domain.refund.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    List<Refund> findRefundByStatus(RefundStatus status);
}
