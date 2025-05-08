package me.performancereservation.domain.refund;

import lombok.extern.slf4j.Slf4j;
import me.performancereservation.domain.refund.dto.RefundRequest;
import me.performancereservation.domain.refund.dto.RefundResponse;

@Slf4j
public class RefundTestUtils {

    public static void logRefundRequest(RefundRequest request, String prefix) {
        log.info("{}: reservationId={}, userId={}, account={}, bank={}, status={}",
                prefix,
                request.getReservationId(),
                request.getUserId(),
                request.getAccount(),
                request.getBank(),
                request.getStatus());
    }

    public static void logRefundEntity(Refund refund, String prefix) {
        log.info("{}: id={}, reservationId={}, userId={}, account={}, bank={}, status={}, createdAt={}, updatedAt={}",
                prefix,
                refund.getId(),
                refund.getReservationId(),
                refund.getUserId(),
                refund.getAccount(),
                refund.getBank(),
                refund.getStatus(),
                refund.getCreatedAt(),
                refund.getUpdatedAt());
    }

    public static void logRefundResponse(RefundResponse response, String prefix) {
        log.info("{}: refundId={}, reservationId={}, account={}, bank={}, status={}",
                prefix,
                response.getRefundId(),
                response.getReservationId(),
                response.getAccount(),
                response.getBank(),
                response.getStatus());
    }
}
