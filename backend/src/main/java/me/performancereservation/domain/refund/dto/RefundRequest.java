package me.performancereservation.domain.refund.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundRequest {

    private Long reservationId; // (FK) 환불을 요청하는 예약 id

    private Long userId; // (FK) 환불을 요청한 유저의 id

    private String account; // 환불 받을 계좌번호

    private String bank; // 환불 받을 은행

}
