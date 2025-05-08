package me.performancereservation.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.performancereservation.domain.refund.RefundService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final RefundService refundService;

    // 모든 환불 리스트 반환

    // PENDING 환불 리스트 반환

    // 환불 상태 변경 (PENDING-> CONFIRMED)
    // 예약 상태도 변경 (CANCEL_PENDING-> CANCEL_CONFIRMED)
    // 요청 from ADMIN

}
