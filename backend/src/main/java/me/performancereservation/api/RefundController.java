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

    /*--- USER 요청에 대응 ---*/
    // 본인 id와 일치하는 모든 환불내역 리스트 반환

    // 특정 예약id 환불요청 -> 환불내역 생성


    /*--- ADMIN 요청에 대응 ---*/
    // 모든 환불 리스트 반환

    // 모든 환불 내역 중 특정 status 리스트 반환

    // 환불 상태 변경 (환불 승인) PENDING-> CONFIRMED
    // 예약 상태도 변경 (CANCEL_PENDING-> CANCEL_CONFIRMED)


}
