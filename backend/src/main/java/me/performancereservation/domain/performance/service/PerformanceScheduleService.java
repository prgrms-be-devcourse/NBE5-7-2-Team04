package me.performancereservation.domain.performance.service;

import lombok.RequiredArgsConstructor;
import me.performancereservation.domain.performance.dto.performanceschedule.PerformanceScheduleRequest;
import me.performancereservation.domain.performance.entities.Performance;
import me.performancereservation.domain.performance.entities.PerformanceSchedule;
import me.performancereservation.domain.performance.enums.PerformanceStatus;
import me.performancereservation.domain.performance.mapper.PerformanceScheduleMapper;
import me.performancereservation.domain.performance.repository.PerformanceRepository;
import me.performancereservation.domain.performance.repository.PerformanceScheduleRepository;
import me.performancereservation.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerformanceScheduleService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceScheduleRepository performanceScheduleRepository;

    /** 회차 등록
     *
     * 등록된 공연에 대한 회차를 등록
     * 공연 존재 여부를 검사(공연 승인 상태 검사)한 뒤 등록한 회차를 해당 공연에 연결
     * @param performanceId
     * @param request
     * @return performanceScheduleId
     */
    @Transactional
    public Long createPerformanceSchedule(Long performanceId, PerformanceScheduleRequest request) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> ErrorCode.PERFORMANCE_NOT_FOUND.domainException("id=" + performanceId));

        // 해당 공연이 존재하고 관리자에게 승인을 받은 상태인지 확인
        if(!(performance.getStatus() == PerformanceStatus.CONFIRMED)) {
            throw ErrorCode.PERFORMANCE_PENDING_APPROVAL
                    .domainException("performanceId=" + performanceId + "는 승인 대기 상태");
        }

        PerformanceSchedule schedule = PerformanceScheduleMapper.toEntity(request, performanceId, performance.getTotalSeats());
        return performanceScheduleRepository.save(schedule).getId();
    }


    /** 특정 회차 공연 취소
     *
     * 취소 후 회차 id 반환
     * @param performanceScheduleId
     * @return scheduleId
     */
    @Transactional
    public Long cancelPerformanceSchedule(Long performanceScheduleId) {
        PerformanceSchedule schedule = performanceScheduleRepository.findById(performanceScheduleId)
                .orElseThrow(() -> ErrorCode.PERFORMANCE_SCHEDULE_NOT_FOUND.domainException("id=" + performanceScheduleId));

        schedule.cancel();
        return schedule.getId();
    }
}
