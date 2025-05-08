package me.performancereservation.domain.refund;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.performancereservation.domain.refund.dto.RefundRequest;
import me.performancereservation.domain.refund.dto.RefundResponse;
import me.performancereservation.domain.refund.enums.RefundStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;

    // refundRequest를 받고 Refund를 생성하여 저장
    public Long save(RefundRequest refundRequest) {

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
        List<RefundResponse> refundListDto = foundRefunds.stream()
                .map(RefundResponse::fromEntity) //RefundResponse 내부의 formEntity 메서드로 각각 변환
                .collect(Collectors.toList()); // stream-> list로 변환
        return refundListDto;
    }

    // 상태별 refund 목록 조회
    public List<RefundResponse> findRefundByStatus(RefundStatus status) {
        List<Refund> foundRefunds = refundRepository.findRefundByStatus(status);
        return foundRefunds.stream()
                .map(RefundResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // CONFIRMED refund 목록 조회

    // 환불 상태 변경 PENDING -> CONFIRMED

}
