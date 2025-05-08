package me.performancereservation.domain.refund;

import lombok.extern.slf4j.Slf4j;
import me.performancereservation.domain.refund.dto.RefundRequest;
import me.performancereservation.domain.refund.dto.RefundResponse;
import me.performancereservation.domain.refund.enums.RefundStatus;
import me.performancereservation.global.exception.AppException;
import me.performancereservation.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class RefundServiceTest {

    @Autowired
    private RefundService refundService;

    @Autowired
    private RefundRepository refundRepository;

    private RefundRequest refundRequest;
    private Refund refund;

    @BeforeEach
    void setUp() {
        log.info("테스트 설정 시작");
        refundRequest = RefundRequest.builder()
                .reservationId(1L)
                .userId(1L)
                .account("123-456-789")
                .bank("신한은행")
                .status(RefundStatus.PENDING)
                .build();

        refund = Refund.builder()
                .id(1L)
                .reservationId(1L)
                .userId(1L)
                .account("123-456-789")
                .bank("신한은행")
                .status(RefundStatus.PENDING)
                .build();
        log.info("테스트 설정 완료: refundRequest={}, refund={}", refundRequest, refund);
    }

    @Test
    @DisplayName("save 테스트")
    void saveTest() throws Exception {
        // given
        log.info("환불 저장 테스트 시작");
        RefundTestUtils.logRefundRequest(refundRequest, "저장할 환불 요청 정보");

        // when
        Long savedId = refundService.save(refundRequest);
        log.info("환불 저장 완료: savedId={}", savedId);

        // then
        Refund savedRefund = refundRepository.findById(savedId).orElseThrow();
        RefundTestUtils.logRefundEntity(savedRefund, "DB에서 조회한 환불 정보");

        assertThat(savedId).isNotNull();
        assertThat(savedRefund.getReservationId()).isEqualTo(refundRequest.getReservationId());
        assertThat(savedRefund.getUserId()).isEqualTo(refundRequest.getUserId());
        assertThat(savedRefund.getAccount()).isEqualTo(refundRequest.getAccount());
        assertThat(savedRefund.getBank()).isEqualTo(refundRequest.getBank());
        assertThat(savedRefund.getStatus()).isEqualTo(refundRequest.getStatus());
        log.info("환불 저장 테스트 완료");
    }

    @Test
    @DisplayName("전체 환불 목록 조회 테스트")
    void findAllRefundsTest() throws Exception {
        // given
        log.info("환불 목록 조회 테스트 시작");

        // 첫 번째 환불 저장
        Long savedId1 = refundService.save(refundRequest);
        log.info("첫 번째 환불 저장 완료: savedId={}", savedId1);

        // 두 번째 환불 저장
        RefundRequest refundRequest2 = RefundRequest.builder()
                .reservationId(2L)
                .userId(2L)
                .account("987-654-321")
                .bank("국민은행")
                .status(RefundStatus.PENDING)
                .build();
        Long savedId2 = refundService.save(refundRequest2);
        log.info("두 번째 환불 저장 완료: savedId={}", savedId2);

        // when
        List<RefundResponse> refundResponses = refundService.findAllRefunds();
        log.info("환불 목록 조회 완료: size={}", refundResponses.size());

        // then
        assertThat(refundResponses).hasSize(2);

        // 첫 번째 환불 검증
        RefundResponse firstRefund = refundResponses.get(0);
        RefundTestUtils.logRefundResponse(firstRefund, "첫 번째 환불 정보");

        assertThat(firstRefund.getReservationId()).isEqualTo(refundRequest.getReservationId());
        assertThat(firstRefund.getAccount()).isEqualTo(refundRequest.getAccount());
        assertThat(firstRefund.getBank()).isEqualTo(refundRequest.getBank());
        assertThat(firstRefund.getStatus()).isEqualTo(refundRequest.getStatus());

        // 두 번째 환불 검증
        RefundResponse secondRefund = refundResponses.get(1);
        RefundTestUtils.logRefundResponse(secondRefund, "두 번째 환불 정보");

        assertThat(secondRefund.getReservationId()).isEqualTo(refundRequest2.getReservationId());
        assertThat(secondRefund.getAccount()).isEqualTo(refundRequest2.getAccount());
        assertThat(secondRefund.getBank()).isEqualTo(refundRequest2.getBank());
        assertThat(secondRefund.getStatus()).isEqualTo(refundRequest2.getStatus());

        log.info("환불 목록 조회 테스트 완료");
    }

    @Test
    @DisplayName("환불 상태별 목록 조회")
    void findRefundByStatusTest() throws Exception {
        // given
        log.info("환불 상태별 조회 테스트 시작");

        // 첫 번째 PENDING 상태의 환불 저장
        RefundRequest pendingRequest1 = RefundRequest.builder()
                .reservationId(1L)
                .userId(1L)
                .account("123-456-789")
                .bank("신한은행")
                .status(RefundStatus.PENDING)
                .build();
        Long pendingId1 = refundService.save(pendingRequest1);
        log.info("첫 번째 PENDING 상태 환불 저장 완료: savedId={}", pendingId1);

        // 두 번째 PENDING 상태의 환불 저장
        RefundRequest pendingRequest2 = RefundRequest.builder()
                .reservationId(2L)
                .userId(2L)
                .account("987-654-321")
                .bank("국민은행")
                .status(RefundStatus.PENDING)
                .build();
        Long pendingId2 = refundService.save(pendingRequest2);
        log.info("두 번째 PENDING 상태 환불 저장 완료: savedId={}", pendingId2);

        // CONFIRMED 상태의 환불 저장
        RefundRequest confirmedRequest = RefundRequest.builder()
                .reservationId(3L)
                .userId(3L)
                .account("111-222-333")
                .bank("우리은행")
                .status(RefundStatus.CONFIRMED)
                .build();
        Long confirmedId = refundService.save(confirmedRequest);
        log.info("CONFIRMED 상태 환불 저장 완료: savedId={}", confirmedId);

        // when
        List<RefundResponse> pendingRefunds = refundService.findRefundByStatus(RefundStatus.PENDING);
        List<RefundResponse> confirmedRefunds = refundService.findRefundByStatus(RefundStatus.CONFIRMED);

        log.info("PENDING 상태 환불 목록 조회 완료: size={}", pendingRefunds.size());
        log.info("CONFIRMED 상태 환불 목록 조회 완료: size={}", confirmedRefunds.size());

        // then
        // PENDING 상태 환불 검증
        assertThat(pendingRefunds).hasSize(2);

        // 첫 번째 PENDING 환불 검증
        RefundResponse pendingRefund1 = pendingRefunds.get(0);
        RefundTestUtils.logRefundResponse(pendingRefund1, "첫 번째 PENDING 상태 환불 정보");

        assertThat(pendingRefund1.getReservationId()).isEqualTo(pendingRequest1.getReservationId());
        assertThat(pendingRefund1.getAccount()).isEqualTo(pendingRequest1.getAccount());
        assertThat(pendingRefund1.getBank()).isEqualTo(pendingRequest1.getBank());
        assertThat(pendingRefund1.getStatus()).isEqualTo(RefundStatus.PENDING);

        // 두 번째 PENDING 환불 검증
        RefundResponse pendingRefund2 = pendingRefunds.get(1);
        RefundTestUtils.logRefundResponse(pendingRefund2, "두 번째 PENDING 상태 환불 정보");

        assertThat(pendingRefund2.getReservationId()).isEqualTo(pendingRequest2.getReservationId());
        assertThat(pendingRefund2.getAccount()).isEqualTo(pendingRequest2.getAccount());
        assertThat(pendingRefund2.getBank()).isEqualTo(pendingRequest2.getBank());
        assertThat(pendingRefund2.getStatus()).isEqualTo(RefundStatus.PENDING);

        // CONFIRMED 상태 환불 검증
        assertThat(confirmedRefunds).hasSize(1);
        RefundResponse confirmedRefund = confirmedRefunds.get(0);
        RefundTestUtils.logRefundResponse(confirmedRefund, "CONFIRMED 상태 환불 정보");

        assertThat(confirmedRefund.getReservationId()).isEqualTo(confirmedRequest.getReservationId());
        assertThat(confirmedRefund.getAccount()).isEqualTo(confirmedRequest.getAccount());
        assertThat(confirmedRefund.getBank()).isEqualTo(confirmedRequest.getBank());
        assertThat(confirmedRefund.getStatus()).isEqualTo(RefundStatus.CONFIRMED);

        log.info("환불 상태별 조회 테스트 완료");
    }

    @Test
    @DisplayName("환불상태변경 테스트")
    void updateRefundStatusTest() throws Exception {
        // given
        log.info("환불 상태 변경 테스트 시작");
        
        // 환불 요청 저장
        Long savedId = refundService.save(refundRequest);
        log.info("환불 요청 저장 완료: savedId={}", savedId);
        
        // when
        RefundStatus newStatus = RefundStatus.CONFIRMED;
        refundService.updateRefundStatus(savedId, newStatus);
        log.info("환불 상태 변경 완료: newStatus={}", newStatus);
        
        // then
        Refund updatedRefund = refundRepository.findById(savedId).orElseThrow();
        RefundTestUtils.logRefundEntity(updatedRefund, "상태가 변경된 환불 정보");
        
        assertThat(updatedRefund.getStatus()).isEqualTo(newStatus);
        assertThat(updatedRefund.getReservationId()).isEqualTo(refundRequest.getReservationId());
        assertThat(updatedRefund.getUserId()).isEqualTo(refundRequest.getUserId());
        assertThat(updatedRefund.getAccount()).isEqualTo(refundRequest.getAccount());
        assertThat(updatedRefund.getBank()).isEqualTo(refundRequest.getBank());
        
        log.info("환불 상태 변경 테스트 완료");
    }

    /*------------- 실패 테스트 ------------*/

    @Test
    @DisplayName("존재하지 않는 환불 ID로 상태 변경 시도시 예외 발생")
    void updateRefundStatusWithInvalidIdTest() {
        // given
        log.info("존재하지 않는 환불 ID 테스트 시작");
        Long invalidId = 999L;
        RefundStatus newStatus = RefundStatus.CONFIRMED;

        /* Service 측에서 AppException(ErrorCode.NO_SUCH_REFUND_ERROR, ErrorType.DOMAIN))을 던진다
            updateRefundStatus에서 던져진 예외가 AppException 클래스가 아니거나
            errorCode 필드의 내용이 아래와 같지 않으면 assert */
        assertThatThrownBy(() -> refundService.updateRefundStatus(invalidId, newStatus))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NO_SUCH_REFUND_ERROR);

        log.info("존재하지 않는 환불 ID 테스트 완료");
    }

    @Test
    @DisplayName("이미 존재하는 예약 ID로 환불 요청 시도시 예외 발생")
    void saveWithDuplicateReservationIdTest() {
        // given
        log.info("중복 예약 ID 테스트 시작");
        
        // 첫 번째 환불 요청 저장
        Long savedId = refundService.save(refundRequest);
        log.info("첫 번째 환불 요청 저장 완료: savedId={}", savedId);
        
        // 같은 예약 ID로 두 번째 환불 요청 생성
        RefundRequest duplicateRequest = RefundRequest.builder()
                .reservationId(refundRequest.getReservationId()) // 동일한 예약 ID
                .userId(2L) // 다른 사용자 ID
                .account("987-654-321")
                .bank("국민은행")
                .status(RefundStatus.PENDING)
                .build();
        
        // when & then
        assertThatThrownBy(() -> refundService.save(duplicateRequest))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_REFUND_ERROR);
        
        log.info("중복 예약 ID 테스트 완료");
    }

}