package me.performancereservation.domain.performance.service;

import me.performancereservation.domain.file.File;
import me.performancereservation.domain.file.FileRepository;
import me.performancereservation.domain.performance.dto.performance.request.PerformanceCreateRequest;
import me.performancereservation.domain.performance.dto.performance.request.PerformanceUpdateRequest;
import me.performancereservation.domain.performance.entities.Performance;
import me.performancereservation.domain.performance.entities.PerformanceSchedule;
import me.performancereservation.domain.performance.enums.PerformanceCategory;
import me.performancereservation.domain.performance.enums.PerformanceStatus;
import me.performancereservation.domain.performance.repository.PerformanceRepository;
import me.performancereservation.domain.performance.repository.PerformanceScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @Mock
    PerformanceRepository performanceRepository;

    @Mock
    PerformanceScheduleRepository performanceScheduleRepository;

    @Mock
    FileRepository fileRepository;

    @InjectMocks
    PerformanceService performanceService;

    static final Long PERFORMANCE_ID = 1L;
    static final Long PERFORMANCE_SCHEDULE_ID1 = 1L;
    static final Long PERFORMANCE_SCHEDULE_ID2 = 2L;
    static final Long PERFORMANCE_SCHEDULE_ID3 = 3L;
    static final Long FILE_ID = 1L;

    Performance performance;
    PerformanceSchedule schedule1;
    PerformanceSchedule schedule2;
    PerformanceSchedule schedule3;

    @BeforeEach
    void init() {
        performance = Performance.builder()
                .id(PERFORMANCE_ID)
                .title("오페라 갈라")
                .venue("세종문화회관 대극장")
                .price(120000)
                .totalSeats(2000)
                .category(PerformanceCategory.OPERA)
                .performanceDate(LocalDateTime.of(2025, 12, 13, 0, 0))
                .description("한자리에서 만나는 오페라 명곡들 그리고 오페라 스타들!")
                .fileId(FILE_ID)
                .build();

        schedule1 = PerformanceSchedule.builder()
                .id(PERFORMANCE_SCHEDULE_ID1)
                .performanceId(PERFORMANCE_ID)
                .startTime(LocalDateTime.of(2025, 12, 13, 9, 0))
                .endTime(LocalDateTime.of(2025, 12, 13, 10, 0))
                .canceled(false)
                .build();

        schedule2 = PerformanceSchedule.builder()
                .id(PERFORMANCE_SCHEDULE_ID2)
                .performanceId(PERFORMANCE_ID)
                .startTime(LocalDateTime.of(2025, 12, 13, 11, 0))
                .endTime(LocalDateTime.of(2025, 12, 13, 12, 0))
                .canceled(false)
                .build();

        schedule3 = PerformanceSchedule.builder()
                .id(PERFORMANCE_SCHEDULE_ID3)
                .performanceId(PERFORMANCE_ID)
                .startTime(LocalDateTime.of(2025, 12, 13, 13, 0))
                .endTime(LocalDateTime.of(2025, 12, 13, 14, 0))
                .canceled(false)
                .build();
    }

    @Test
    @DisplayName("공연 생성 성공 테스트")
    void createPerformance_Success() {
        //given
        PerformanceCreateRequest request = new PerformanceCreateRequest(
                "오페라 갈라",
                "세종문화회관 대극장",
                120000,
                2000,
                "OPERA",
                LocalDateTime.of(2025,12,13, 0,0),
                "한자리에서 만나는 오페라 명곡들 그리고 오페라 스타들!"
        );
        when(performanceRepository.save(any(Performance.class))).thenReturn(performance);

        //when
        Long savedId = performanceService.createPerformance(request);

        //then
        assertThat(savedId).isNotNull();
        assertThat(savedId).isEqualTo(PERFORMANCE_ID);
        verify(performanceRepository).save(any(Performance.class));
    }

    @Test
    @DisplayName("공연 수정 성공 테스트")
    void updatePerformance_Success() {
        //given
        File file = File.builder()
                .id(FILE_ID)
                .key("파일url")
                .build();

        PerformanceUpdateRequest request = new PerformanceUpdateRequest(
                file.getId(),
                "변경된 설명"
        );

        when(performanceRepository.findById(PERFORMANCE_ID)).thenReturn(Optional.of(performance));

        //when
        Long updatedId = performanceService.updatePerformance(performance.getId(), request);

        //then
        assertThat(updatedId).isEqualTo(PERFORMANCE_ID);
        assertThat(performance.getFileId()).isEqualTo(request.fileId());
        assertThat(performance.getDescription()).isEqualTo(request.description());
    }

    @Test
    @DisplayName("공연 취소 성공 테스트")
    void cancelPerformance_Success() {
        //given
        when(performanceRepository.findById(PERFORMANCE_ID)).thenReturn(Optional.of(performance));

        when(performanceScheduleRepository.findByPerformanceId(PERFORMANCE_ID))
                .thenReturn(List.of(schedule1, schedule2, schedule3));

        //when
        performanceService.cancelPerformance(PERFORMANCE_ID);

        //then
        assertThat(performance.getStatus()).isEqualTo(PerformanceStatus.CANCELLED);
        assertThat(schedule1.isCanceled()).isTrue();
        assertThat(schedule2.isCanceled()).isTrue();
        assertThat(schedule3.isCanceled()).isTrue();
        verify(performanceScheduleRepository).findByPerformanceId(PERFORMANCE_ID);
    }

}