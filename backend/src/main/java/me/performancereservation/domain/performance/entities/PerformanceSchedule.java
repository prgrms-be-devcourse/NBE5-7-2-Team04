package me.performancereservation.domain.performance.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.performancereservation.domain.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PerformanceSchedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long performanceId; // (FK) 공연 ID

    private LocalDateTime startTime; // 공연 시작 시간

    private LocalDateTime endTime; // 공연 종료 시간

    private int remainingSeats; // 남은 좌석

    @Column(name = "is_canceled")
    private boolean canceled; // 회차 취소 여부

    @Builder
    public PerformanceSchedule(Long id, Long performanceId, LocalDateTime startTime, LocalDateTime endTime, int remainingSeats, boolean canceled) {
        this.id = id;
        this.performanceId = performanceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remainingSeats = remainingSeats;
        this.canceled = canceled;
    }

    public void cancel() {
        if (!this.canceled) {
            this.canceled = true;
        }
    }
}