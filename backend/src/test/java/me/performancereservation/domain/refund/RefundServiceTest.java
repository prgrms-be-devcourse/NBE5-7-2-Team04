package me.performancereservation.domain.refund;

import lombok.extern.slf4j.Slf4j;
import me.performancereservation.domain.performance.entities.Performance;
import me.performancereservation.domain.performance.entities.PerformanceSchedule;
import me.performancereservation.domain.performance.enums.PerformanceCategory;
import me.performancereservation.domain.performance.enums.PerformanceStatus;
import me.performancereservation.domain.performance.enums.ScheduleStatus;
import me.performancereservation.domain.performance.repository.PerformanceRepository;
import me.performancereservation.domain.performance.repository.PerformanceScheduleRepository;
import me.performancereservation.domain.refund.dto.RefundRequest;
import me.performancereservation.domain.refund.dto.RefundResponse;
import me.performancereservation.domain.refund.dto.RefundDetailResponse;
import me.performancereservation.domain.refund.enums.RefundStatus;
import me.performancereservation.domain.reservation.Reservation;
import me.performancereservation.domain.reservation.ReservationRepository;
import me.performancereservation.domain.reservation.enums.ReservationStatus;
import me.performancereservation.global.exception.AppException;
import me.performancereservation.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PerformanceScheduleRepository performanceScheduleRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    private RefundRequest refundRequest;
    private Refund refund;
    private Performance performance;
    private PerformanceSchedule schedule;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        log.info("테스트 설정 시작");
        
        // Performance 생성
        performance = Performance.builder()
                .title("테스트 공연")
                .venue("테스트 공연장")
                .price(10000)
                .category(PerformanceCategory.OPERA)
                .description("테스트 공연 설명")
                .performance_date(LocalDateTime.now())
                .status(PerformanceStatus.CONFIRMED)
                .build();
        performance = performanceRepository.save(performance);
        log.info("Performance 저장 완료: id={}", performance.getId());

        // PerformanceSchedule 생성
        schedule = PerformanceSchedule.builder()
                .performanceId(performance.getId())
                .startTime(LocalDateTime.now().plusDays(7))
                .endTime(LocalDateTime.now().plusDays(7).plusHours(2))
                .remainingSeats(100)
                .is_canceled(false)
                .build();
        schedule = performanceScheduleRepository.save(schedule);
        log.info("PerformanceSchedule 저장 완료: id={}", schedule.getId());

        // Reservation 생성
        reservation = Reservation.builder()
                .userId(1L)
                .scheduleId(schedule.getId())
                .quantity(2)
                .status(ReservationStatus.CANCEL_PENDING)
                .build();
        reservation = reservationRepository.save(reservation);
        log.info("첫 번째 예약 저장 완료: id={}", reservation.getId());

        // RefundRequest 생성
        refundRequest = RefundRequest.builder()
                .reservationId(reservation.getId())
                .userId(1L)
                .account("123-456-789")
                .bank("신한은행")
                .status(RefundStatus.PENDING)
                .build();

        refund = Refund.builder()
                .id(1L)
                .reservationId(reservation.getId())
                .userId(1L)
                .account("123-456-789")
                .bank("신한은행")
                .status(RefundStatus.PENDING)
                .build();
        log.info("테스트 설정 완료: refundRequest={}", refundRequest);
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
    void findAllRefundByStatusTest() throws Exception {
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
        List<RefundResponse> pendingRefunds = refundService.findAllRefundByStatus(RefundStatus.PENDING);
        List<RefundResponse> confirmedRefunds = refundService.findAllRefundByStatus(RefundStatus.CONFIRMED);

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
    @DisplayName("전체 환불 상세 정보 조회 테스트")
    void findAllRefundsDetailTest() throws Exception {
        // given
        log.info("환불 상세 정보 조회 테스트 시작");

        // 첫 번째 환불 저장
        Long savedId1 = refundService.save(refundRequest);
        log.info("첫 번째 환불 저장 완료: savedId={}", savedId1);

        // 두 번째 예약 생성
        Reservation reservation2 = Reservation.builder()
                .userId(2L)
                .scheduleId(schedule.getId())
                .quantity(1)
                .status(ReservationStatus.CANCEL_PENDING)
                .build();
        reservation2 = reservationRepository.save(reservation2);
        log.info("다른 유저id로 두 번째 예약 저장 완료: id={}", reservation2.getId());

        // 두 번째 환불 저장
        RefundRequest refundRequest2 = RefundRequest.builder()
                .reservationId(reservation2.getId())
                .userId(2L)
                .account("987-654-321")
                .bank("국민은행")
                .status(RefundStatus.PENDING)
                .build();
        Long savedId2 = refundService.save(refundRequest2);
        log.info("두 번째 환불 저장 완료: savedId={}", savedId2);

        // when
        List<RefundDetailResponse> refundDetailResponses = refundService.findAllRefundsDetail();
        log.info("환불 상세 정보 조회 완료: size={}", refundDetailResponses.size());

        // then
        assertThat(refundDetailResponses).hasSize(2);

        // 첫 번째 환불 상세 정보 검증
        RefundDetailResponse firstRefundDetail = refundDetailResponses.get(0);
        RefundTestUtils.logRefundDetailResponse(firstRefundDetail, "첫 번째 환불 상세 정보");
        RefundTestUtils.assertRefundDetailResponse(firstRefundDetail, refundRequest, reservation, schedule, performance);

        // 두 번째 환불 상세 정보 검증
        RefundDetailResponse secondRefundDetail = refundDetailResponses.get(1);
        RefundTestUtils.logRefundDetailResponse(secondRefundDetail, "두 번째 환불 상세 정보");
        RefundTestUtils.assertRefundDetailResponse(secondRefundDetail, refundRequest2, reservation2, schedule, performance);

        log.info("환불 상세 정보 조회 테스트 완료");
    }

    /**
     * 테스트 시나리오:
     * 특정 사용자(userId = 1L)의 환불 2건 생성.
     * 다른 사용자(userId = 2L)의 환불 1건 생성.
     * 특정 사용자의 환불 상세 정보만 조회되는지 확인
     * //
     * 검증 포인트:
     * 조회된 환불 상세 정보의 개수가 2개인지 확인.
     * 각 환불 상세 정보의 내용이 올바른지 검증.
     * 다른 사용자의 환불 정보가 포함되지 않는지 확인.
     *
     */
    @Test
    @DisplayName("특정 사용자의 환불 상세 정보 조회 테스트")
    void findAllRefundsDetailByUserIdTest() throws Exception {
        // given
        log.info("특정 사용자의 환불 상세 정보 조회 테스트 시작");
        Long userId = 1L;

        // 첫 번째 환불 저장
        Long savedId1 = refundService.save(refundRequest);
        log.info("첫 번째 환불 저장 완료: savedId={}", savedId1);

        // 두 번째 예약 생성 (같은 사용자)
        Reservation reservation2 = Reservation.builder()
                .userId(userId)
                .scheduleId(schedule.getId())
                .quantity(1)
                .status(ReservationStatus.CANCEL_PENDING)
                .build();
        reservation2 = reservationRepository.save(reservation2);
        log.info("같은 사용자의 두 번째 예약 저장 완료: id={}", reservation2.getId());

        // 두 번째 환불 저장
        RefundRequest refundRequest2 = RefundRequest.builder()
                .reservationId(reservation2.getId())
                .userId(userId)
                .account("987-654-321")
                .bank("국민은행")
                .status(RefundStatus.PENDING)
                .build();
        Long savedId2 = refundService.save(refundRequest2);
        log.info("두 번째 환불 저장 완료: savedId={}", savedId2);

        // 다른 사용자의 예약 생성
        Reservation otherUserReservation = Reservation.builder()
                .userId(2L)
                .scheduleId(schedule.getId())
                .quantity(3)
                .status(ReservationStatus.CANCEL_PENDING)
                .build();
        otherUserReservation = reservationRepository.save(otherUserReservation);
        log.info("다른 사용자의 예약 저장 완료: id={}", otherUserReservation.getId());

        // 다른 사용자의 환불 저장
        RefundRequest otherUserRefundRequest = RefundRequest.builder()
                .reservationId(otherUserReservation.getId())
                .userId(2L)
                .account("111-222-333")
                .bank("우리은행")
                .status(RefundStatus.PENDING)
                .build();
        Long otherUserSavedId = refundService.save(otherUserRefundRequest);
        log.info("다른 사용자의 환불 저장 완료: savedId={}", otherUserSavedId);

        // when
        List<RefundDetailResponse> userRefundDetails = refundService.findAllRefundsDetailByUserId(userId);
        log.info("특정 사용자의 환불 상세 정보 조회 완료: size={}", userRefundDetails.size());

        // then
        assertThat(userRefundDetails).hasSize(2);

        // 첫 번째 환불 상세 정보 검증
        RefundDetailResponse firstRefundDetail = userRefundDetails.get(0);
        RefundTestUtils.logRefundDetailResponse(firstRefundDetail, "첫 번째 환불 상세 정보");
        RefundTestUtils.assertRefundDetailResponse(firstRefundDetail, refundRequest, reservation, schedule, performance);

        // 두 번째 환불 상세 정보 검증
        RefundDetailResponse secondRefundDetail = userRefundDetails.get(1);
        RefundTestUtils.logRefundDetailResponse(secondRefundDetail, "두 번째 환불 상세 정보");
        RefundTestUtils.assertRefundDetailResponse(secondRefundDetail, refundRequest2, reservation2, schedule, performance);

        log.info("특정 사용자의 환불 상세 정보 조회 테스트 완료");
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