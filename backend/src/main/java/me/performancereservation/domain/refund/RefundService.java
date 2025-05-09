package me.performancereservation.domain.refund;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.performancereservation.domain.performance.entities.Performance;
import me.performancereservation.domain.refund.dto.RefundDetailResponse;
import me.performancereservation.domain.refund.dto.RefundRequest;
import me.performancereservation.domain.refund.dto.RefundResponse;
import me.performancereservation.domain.refund.enums.RefundStatus;
import me.performancereservation.domain.reservation.Reservation;
import me.performancereservation.domain.reservation.ReservationRepository;
import me.performancereservation.domain.reservation.enums.ReservationStatus;
import me.performancereservation.global.exception.AppException;
import me.performancereservation.global.exception.ErrorCode;
import me.performancereservation.global.exception.ErrorType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final ReservationRepository reservationRepository;

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

        // refund 저장 후 설정된 id 기록
        Long savedRefundId = refundRepository.save(newRefund).getId();

        // 예약 : CANCEL_PENDING 상태로 변경
        Reservation reservation = reservationRepository.findById(refundRequest.getReservationId()).orElseThrow();
        reservation.setStatus(ReservationStatus.CANCEL_PENDING);

        return savedRefundId;
    }

    // 모든 id의 환불내역 디테일 조회
    public List<RefundDetailResponse> findAllRefundsDetail() {
        // 쿼리로 [Refund, 예약수량, 시작시간, 회차상태, Performance]의 리스트를 받아옴
        List<Object[]> results = refundRepository.findAllRefundsWithDetails();
        return getRefundDetailResponses(results);
    }

    // 입력받은 id의 환불내역 디테일 조회
    public List<RefundDetailResponse> findAllRefundsDetailByUserId(Long userId) {
        List<Object[]> results = refundRepository.findRefundsDetailByUserId(userId);
        return getRefundDetailResponses(results);
    }

    // 특정 status의 환불내역 디테일 조회
    public List<RefundDetailResponse> findAllRefundsDetailByRefundStatus(RefundStatus refundStatus) {
        List<Object[]> results = refundRepository.findRefundsDetailByStatus(refundStatus);
        return getRefundDetailResponses(results);
    }

    // 쿼리로 받아온 Object[] => RefundDetailResponse로 변환하는 메서드
    private List<RefundDetailResponse> getRefundDetailResponses(List<Object[]> results) {
        return results.stream()
                .map(result -> {
                    Refund refund = (Refund) result[0];
                    Integer reservationQuantity = (Integer) result[1];
                    LocalDateTime startTime = (LocalDateTime) result[2];
                    Performance performance = (Performance) result[3];

                    return RefundDetailResponse.fromEntity(refund, reservationQuantity, startTime, performance);
                })
                .collect(Collectors.toList());
    }

    // 전체 refund 목록 조회 (간단한 내용)
    public List<RefundResponse> findAllRefunds() {
        List<Refund> foundRefunds = refundRepository.findAll();
        return foundRefunds.stream()
                .map(RefundResponse::fromRefund) //RefundResponse 내부의 formEntity 메서드로 각각 변환
                .collect(Collectors.toList()); // stream-> list로 변환
    }

    // 전체 refund 목록 상태별 조회 (간단한 내용)
    public List<RefundResponse> findAllRefundByStatus(RefundStatus status) {
        List<Refund> foundRefunds = refundRepository.findRefundByStatus(status);
        return foundRefunds.stream()
                .map(RefundResponse::fromRefund)
                .collect(Collectors.toList());
    }

    // 환불 상태 변경
    public void updateRefundStatus(Long id, RefundStatus status) {
        // id로 먼저 찾아보고 해당하는 Refund가 없다면 throw NO_SUCH_REFUND_ERROR
        Refund refund = refundRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NO_SUCH_REFUND_ERROR, ErrorType.DOMAIN));
        
        // 레포지토리의 메서드를 호출하여 상태 업데이트
        int updatedRows = refundRepository.updateRefundStatus(id, status);
        if (updatedRows == 0) {
            throw new AppException(ErrorCode.NO_SUCH_REFUND_ERROR, ErrorType.DOMAIN);
        }

        // 해당하는 예약을 찾아 CANCEL_CONFIRMED로 상태 변경
        Reservation reservation = reservationRepository.findById(refund.getReservationId()).orElseThrow(); // 나중에 ErrorCode 추가
        reservation.setStatus(ReservationStatus.CANCEL_CONFIRMED);

    }


}
