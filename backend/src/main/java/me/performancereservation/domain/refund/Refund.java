package me.performancereservation.domain.refund;

import jakarta.persistence.*;
import lombok.*;
import me.performancereservation.domain.common.BaseEntity;
import me.performancereservation.domain.refund.enums.RefundStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 환불요청정보 id

    private Long reservationId; // (FK) 환불을 요청하는 예약 id

    private Long userId; // (FK) 환불을 요청한 유저의 id

    private String account; // 환불 받을 계좌번호

    private String bank; // 환불 받을 은행

    @Setter
    @Enumerated(EnumType.STRING)
    private RefundStatus status; // 환불대기, 환불완료 상태 표시

//    public void setStatus(RefundStatus status) {
//        // 유효성검사 추가
//
//        this.status = status;
//    }

    @Builder
    public Refund(Long id, Long reservationId, Long userId, String account, String bank, RefundStatus status) {
        this.id = id;
        this.reservationId = reservationId;
        this.userId = userId;
        this.account = account;
        this.bank = bank;
        this.status = status;
    }
}
