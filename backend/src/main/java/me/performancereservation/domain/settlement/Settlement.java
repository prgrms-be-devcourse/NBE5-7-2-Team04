package me.performancereservation.domain.settlement;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.performancereservation.domain.common.BaseEntity;
import me.performancereservation.domain.settlement.enums.SettlementStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Settlement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 정산 ID

    private Long performanceId; // 공연 ID

    private Long totalAmount; // 총 정산 금액

    private String account; // 계좌번호

    private String bank; // 은행명

    @Enumerated(EnumType.STRING)
    private SettlementStatus status; // 정산 상태

    private LocalDateTime settledAt; // 정산완료일시

    @Builder
    public Settlement(Long id, Long performanceId, Long totalAmount, String account, String bank, SettlementStatus status, LocalDateTime settledAt) {
        this.id = id;
        this.performanceId = performanceId;
        this.totalAmount = totalAmount;
        this.account = account;
        this.bank = bank;
        this.status = status;
        this.settledAt = settledAt;
    }
}
