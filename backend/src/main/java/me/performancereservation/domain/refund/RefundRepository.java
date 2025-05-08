package me.performancereservation.domain.refund;

import me.performancereservation.domain.refund.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    List<Refund> findRefundByStatus(RefundStatus status);

    Optional<Refund> findRefundByReservationId(Long reservationId);

    @Modifying
    @Query("UPDATE Refund r SET r.status = :status WHERE r.id = :id")
    int updateRefundStatus(@Param("id") Long id, @Param("status") RefundStatus status);
}
