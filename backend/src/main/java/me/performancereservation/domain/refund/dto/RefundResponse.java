package me.performancereservation.domain.refund.dto;

import lombok.Builder;
import lombok.Data;
import me.performancereservation.domain.refund.Refund;
import me.performancereservation.domain.refund.enums.RefundStatus;

@Data
@Builder
public class RefundResponse {

    // Refund에서 userId 외의 데이터 전달
    private Long refundId;
    private Long reservationId;
    private String account;
    private String bank;
    private RefundStatus status;

    public static RefundResponse fromEntity(Refund refund) {
        return RefundResponse.builder()
                .refundId(refund.getId())
                .reservationId(refund.getReservationId())
                .account(refund.getAccount())
                .bank(refund.getBank())
                .status(refund.getStatus())
                .build();
    }
}
