package me.performancereservation.domain.refund;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.performancereservation.domain.refund.dto.RefundRequest;
import me.performancereservation.domain.refund.dto.RefundResponse;
import me.performancereservation.domain.refund.enums.RefundStatus;
import me.performancereservation.global.exception.AppException;
import me.performancereservation.global.exception.ErrorCode;
import me.performancereservation.global.exception.ErrorType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;

    // refundRequest를 받고 Refund를 생성하여 저장
    public Long save(RefundRequest refundRequest) {
        // 이미 같은 예약ID인 Refund가 존재한다면 예외 던짐
        if (refundRepository.findRefundByReservationId(refundRequest.getReservationId()).isPresent()) {
            throw new AppException(ErrorCode.DUPLICATE_REFUND_ERROR, ErrorType.DOMAIN);
        }

        Refund newRefund = Refund.builder()
                .reservationId(refundRequest.getReservationId())
                .userId(refundRequest.getUserId())
                .account(refundRequest.getAccount())
                .bank(refundRequest.getBank())
                .status(refundRequest.getStatus())
                .build();

        return refundRepository.save(newRefund).getId();
    }

    // 전체 refund 목록 조회
    public List<RefundResponse> findAllRefunds() {
        List<Refund> foundRefunds = refundRepository.findAll();
        return foundRefunds.stream()
                .map(RefundResponse::fromEntity) //RefundResponse 내부의 formEntity 메서드로 각각 변환
                .collect(Collectors.toList()); // stream-> list로 변환
    }

    // 상태별 refund 목록 조회 (PENDING, CONFIRMED)
    public List<RefundResponse> findRefundByStatus(RefundStatus status) {
        List<Refund> foundRefunds = refundRepository.findRefundByStatus(status);
        return foundRefunds.stream()
                .map(RefundResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 환불 상태 변경
    public void updateRefundStatus(Long id, RefundStatus status) {
        // id로 먼저 찾아보고 해당하는 Refund가 없다면 throw NO_SUCH_REFUND_ERROR
        Refund refund = refundRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NO_SUCH_REFUND_ERROR, ErrorType.DOMAIN));
        refund.setStatus(status);
    }


}
