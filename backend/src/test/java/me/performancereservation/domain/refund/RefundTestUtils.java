package me.performancereservation.domain.refund;

import lombok.extern.slf4j.Slf4j;
import me.performancereservation.domain.performance.entities.Performance;
import me.performancereservation.domain.performance.entities.PerformanceSchedule;
import me.performancereservation.domain.refund.dto.RefundRequest;
import me.performancereservation.domain.refund.dto.RefundResponse;
import me.performancereservation.domain.refund.dto.RefundDetailResponse;
import me.performancereservation.domain.reservation.Reservation;
import org.assertj.core.api.Assertions;
import java.time.temporal.ChronoUnit;

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

    public static void logRefundDetailResponse(RefundDetailResponse response, String message) {
        log.info("=== {} ===", message);
        log.info("환불 ID: {}", response.getRefundId());
        log.info("예약 ID: {}", response.getReservationId());
        log.info("계좌번호: {}", response.getAccount());
        log.info("은행: {}", response.getBank());
        log.info("환불 상태: {}", response.getRefundStatus());
        log.info("예약 수량: {}", response.getQuantity());
        log.info("공연 시작 시간: {}", response.getStartTime());
        log.info("공연 회차 상태: {}", response.getScheduleStatus());
        log.info("공연 제목: {}", response.getTitle());
        log.info("공연 장소: {}", response.getVenue());
        log.info("공연 가격: {}", response.getPrice());
        log.info("공연 분류: {}", response.getCategory());
        log.info("공연 일시: {}", response.getPerformance_date());
        log.info("공연 설명: {}", response.getDescription());
        log.info("==================");
    }

    public static void assertRefundDetailResponse(RefundDetailResponse response, RefundRequest request, Reservation reservation, PerformanceSchedule schedule, Performance performance) {
        Assertions.assertThat(response.getRefundId()).isNotNull();
        Assertions.assertThat(response.getUserId()).isEqualTo(request.getUserId());
        Assertions.assertThat(response.getReservationId()).isEqualTo(request.getReservationId());
        Assertions.assertThat(response.getAccount()).isEqualTo(request.getAccount());
        Assertions.assertThat(response.getBank()).isEqualTo(request.getBank());
        Assertions.assertThat(response.getRefundStatus()).isEqualTo(request.getStatus());
        
        Assertions.assertThat(response.getQuantity()).isEqualTo(reservation.getQuantity());
        
        Assertions.assertThat(response.getStartTime().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(schedule.getStartTime().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertThat(response.getScheduleStatus()).isEqualTo(schedule.getScheduleStatus());

        Assertions.assertThat(response.getFileId()).isEqualTo(performance.getFileId());
        Assertions.assertThat(response.getTitle()).isEqualTo(performance.getTitle());
        Assertions.assertThat(response.getVenue()).isEqualTo(performance.getVenue());
        Assertions.assertThat(response.getPrice()).isEqualTo(performance.getPrice());
        Assertions.assertThat(response.getCategory()).isEqualTo(performance.getCategory());
        Assertions.assertThat(response.getPerformance_date().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo(performance.getPerformance_date().truncatedTo(ChronoUnit.SECONDS));
        Assertions.assertThat(response.getDescription()).isEqualTo(performance.getDescription());
    }
}
