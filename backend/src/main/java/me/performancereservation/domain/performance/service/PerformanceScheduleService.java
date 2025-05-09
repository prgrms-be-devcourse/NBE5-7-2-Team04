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
    public Long createPerformanceSchedule(Long performanceId, PerformanceScheduleRequest request, Long managerId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> ErrorCode.PERFORMANCE_NOT_FOUND.domainException("해당하는 공연을 찾을 수 없습니다. id=" + performanceId));

        // 권한 검사
        if(!hasPermission(managerId, performance)) {
            throw ErrorCode.PERMISSION_DENIED.domainException("등록 권한이 없습니다.");
        }

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
    public Long cancelPerformanceSchedule(Long performanceId, Long performanceScheduleId, Long managerId) {
        // 취소할 회차 검색
        PerformanceSchedule schedule = performanceScheduleRepository.findById(performanceScheduleId)
                .orElseThrow(() -> ErrorCode.PERFORMANCE_SCHEDULE_NOT_FOUND.domainException("해당하는 공연 회차를 찾을 수 없습니다. id=" + performanceScheduleId));

        // 회차가 속한 공연 검색
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> ErrorCode.PERFORMANCE_NOT_FOUND.domainException("해당하는 공연을 찾을 수 없습니다. id=" + performanceId));

        // 공연에 대한 접근 권한 검사
        if(!hasPermission(managerId, performance)) {
            throw ErrorCode.PERMISSION_DENIED.domainException("공연에 대한 권한이 없습니다.");
        }

        // 회차에 대한 접근 권한 검사
        if(!hasSchedulePermission(performance, schedule)) {
            throw ErrorCode.PERMISSION_DENIED.domainException("회차에 대한 권한이 없습니다.");
        }

        schedule.cancel();
        return schedule.getId();
    }

    private static boolean hasSchedulePermission(Performance performance, PerformanceSchedule schedule) {
        return performance.getId().equals(schedule.getId());
    }

    private static boolean hasPermission(Long managerId, Performance performance) {
        return performance.getManagerId().equals(managerId);
    }
}
