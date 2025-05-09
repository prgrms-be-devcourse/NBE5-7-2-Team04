package me.performancereservation.domain.performance.mapper;

import me.performancereservation.domain.performance.dto.performance.request.PerformanceCreateRequest;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceDetailResponse;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceListResponse;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceManagerDetailResponse;
import me.performancereservation.domain.performance.dto.performance.response.PerformanceManagerListResponse;
import me.performancereservation.domain.performance.dto.performanceschedule.PerformanceScheduleResponse;
import me.performancereservation.domain.performance.entities.Performance;
import me.performancereservation.domain.performance.entities.PerformanceSchedule;
import me.performancereservation.domain.performance.enums.PerformanceCategory;
import me.performancereservation.domain.performance.enums.PerformanceStatus;

import java.util.List;

public class PerformanceMapper {

    /** 요청 객체 엔티티 변환
     *
     * 파일 아이디, 공연자 아이디 추가 필요
     * @param request
     * @return Performance
     */
    public static Performance toEntity(PerformanceCreateRequest request, Long fileId, Long managerId) {
        return Performance.builder()
                .fileId(fileId)
                .managerId(managerId)
                .title(request.title())
                .venue(request.venue())
                .price(request.price())
                .totalSeats(request.totalSeats())
                .category(PerformanceCategory.valueOf(request.category()))
                .performanceDate(request.performanceDate())
                .description(request.description())
                .status(PerformanceStatus.PENDING)
                .build();
    }

    /** 공연 상세 페이지용 응답
     *
     * @param performance
     * @param fileUrl
     * @param schedules
     * @return PerformanceDetailResponse
     */
    public static PerformanceDetailResponse toDetailResponse(Performance performance, String fileUrl, List<PerformanceSchedule> schedules) {
        return new PerformanceDetailResponse(
                performance.getId(),
                performance.getTitle(),
                performance.getPrice(),
                performance.getTotalSeats(),
                performance.getVenue(),
                performance.getDescription(),
                performance.getStatus().toString(),
                fileUrl,
                schedules.stream().map(PerformanceScheduleMapper::toResponse).toList()
        );
    }

    /** 공연 목록 페이지 응답용
     *
     * @param performance
     * @param fileUrl
     * @return PerformanceListResponse
     */
    public static PerformanceListResponse toListResponse(Performance performance, String fileUrl) {
        return new PerformanceListResponse(
                performance.getId(),
                fileUrl,
                performance.getTitle(),
                performance.getPrice(),
                performance.getPerformanceDate(),
                performance.getVenue()
        );
    }

    /** 공연자 공연 목록 페이지 응답용
     *
     * @param performance
     * @param fileUrl
     * @return PerformanceManagerListResponse
     */
    public static PerformanceManagerListResponse toManagerListResponse(Performance performance, String fileUrl) {
        return new PerformanceManagerListResponse(
                performance.getId(),
                fileUrl,
                performance.getTitle(),
                performance.getPerformanceDate(),
                performance.getVenue(),
                performance.getStatus().toString()
        );
    }

    /** 공연자 공연 관리 페이지 응답용
     *
     * @param performance
     * @param fileUrl
     * @return PerformanceManagerDetailResponse
     */
    public static PerformanceManagerDetailResponse toManagerDetailResponse(Performance performance,
                                                                           String fileUrl,
                                                                           List<PerformanceScheduleResponse> schedules) {
        return new PerformanceManagerDetailResponse(
                performance.getId(),
                fileUrl,
                performance.getTitle(),
                performance.getVenue(),
                performance.getStatus().toString(),
                performance.getTotalSeats(),
                schedules
        );
    }
}
