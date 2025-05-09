package me.performancereservation.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.performancereservation.domain.refund.RefundService;
import me.performancereservation.domain.refund.dto.RefundDetailResponse;
import me.performancereservation.domain.refund.dto.RefundRequest;
import me.performancereservation.domain.refund.enums.RefundStatus;
import me.performancereservation.global.exception.AppException;
import me.performancereservation.global.exception.ErrorCode;
import me.performancereservation.global.exception.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refunds")
public class RefundController {

    private final RefundService refundService;

    /*--- USER 요청에 대응 ---*/
    // 본인 id와 일치하는 모든 환불내역 리스트 반환
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RefundDetailResponse>> getAllRefundDetailsWithUserId(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(refundService.findAllRefundsDetailByUserId(userId));
    }

    // 특정 예약id 환불요청 -> 환불내역 생성
    @PostMapping("/user/new-refund")
    public ResponseEntity<Long> createNewRefund(@RequestBody RefundRequest refundRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(refundService.save(refundRequest));
    }


    /*--- ADMIN 요청에 대응 ---*/
    // 모든 환불 리스트 반환
    @GetMapping("/admin/all-refunds")
    public ResponseEntity<List<RefundDetailResponse>> getAllRefundDetails() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(refundService.findAllRefundsDetail());
    }

    // 모든 환불 내역 중 특정 status 리스트 반환
    @GetMapping("/admin/{refundStatus}")
    public ResponseEntity<List<RefundDetailResponse>> getAllRefundDetailsByRefundStatus(@PathVariable String refundStatus) {
        try {
            RefundStatus status = RefundStatus.valueOf(refundStatus);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(refundService.findAllRefundsDetailByRefundStatus(status));
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 종류의 refundStatus가 들어왔을 경우
            throw new AppException(ErrorCode.INVALID_REFUND_STATUS, ErrorType.DOMAIN);
        }
    }

    // 환불 상태 변경 (환불 승인) PENDING-> CONFIRMED
    // updateRefundStatus 내부에서 예약 상태도 변경됨 (CANCEL_PENDING-> CANCEL_CONFIRMED)
    @GetMapping("/admin/refund-confirm/{refundId}")
    public ResponseEntity<Void> confirmRefund(@PathVariable Long refundId) {
        refundService.updateRefundStatus(refundId, RefundStatus.CONFIRMED);
        return ResponseEntity.noContent().build();
    }


}
