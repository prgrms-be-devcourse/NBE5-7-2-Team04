package me.performancereservation.domain.refund.dto;

import lombok.Builder;
import lombok.Data;
import me.performancereservation.domain.performance.entities.Performance;
import me.performancereservation.domain.performance.enums.PerformanceCategory;
import me.performancereservation.domain.performance.enums.ScheduleStatus;
import me.performancereservation.domain.refund.Refund;
import me.performancereservation.domain.refund.enums.RefundStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class RefundDetailResponse {

    // Refund에서 userId 외의 데이터 전달
    private Long refundId;
    private Long reservationId;
    private String account;
    private String bank;
    private RefundStatus refundStatus;

    // Reservation에서 가져오는 데이터
    private Integer quantity;

    // PerformanceSchedule에서 가져오는 데이터
    private LocalDateTime startTime;

    // Performance에서 가져오는 데이터 (id, totalSeats 외 모두)
    private Long fileId; // (FK) 파일 ID - 공연 썸네일 용도
    private String title; // 제목
    private String venue; // 공연 장소
    private Integer price; // 가격
    private PerformanceCategory category; // 공연 분류
    private LocalDateTime performance_date; // 공연 일시
    private String description; // 설명
    private ScheduleStatus scheduleStatus;


    public static RefundDetailResponse fromEntity(Refund refund, Integer reservationQuantity, LocalDateTime startTime, ScheduleStatus scheduleStatus, Performance performance) {
        return RefundDetailResponse.builder()
                .refundId(refund.getId())
                .reservationId(refund.getReservationId())
                .account(refund.getAccount())
                .bank(refund.getBank())
                .refundStatus(refund.getStatus())

                .quantity(reservationQuantity)

                .startTime(startTime)
                .scheduleStatus(scheduleStatus)

                .fileId(performance.getFileId())
                .title(performance.getTitle())
                .venue(performance.getVenue())
                .price(performance.getPrice())
                .category(performance.getCategory())
                .performance_date(performance.getPerformance_date())
                .description(performance.getDescription())
                .build();
    }


}
