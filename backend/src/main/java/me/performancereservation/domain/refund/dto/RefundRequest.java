package me.performancereservation.domain.refund.dto;

import lombok.Builder;
import lombok.Data;
import me.performancereservation.domain.refund.enums.RefundStatus;

@Data
@Builder
public class RefundRequest {

    private Long reservationId; // (FK) 환불을 요청하는 예약 id

    private Long userId; // (FK) 환불을 요청한 유저의 id

    private String account; // 환불 받을 계좌번호

    private String bank; // 환불 받을 은행

    private RefundStatus status; // 환불대기, 환불완료 상태 표시

}
