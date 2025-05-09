package me.performancereservation.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 시스템
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // 인증 관련

    // 파일 관련
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드를 실패했습니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "파일 업로드 형식이 잘못되었습니다."),
    MISSING_FILE_PARAMETER(HttpStatus.BAD_REQUEST, "요청에 'file' 파라미터가 포함되어야 합니다."),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드 가능합니다."),
    FILE_EXTENSION_MISSING(HttpStatus.BAD_REQUEST, "확장자가 없는 파일로 요청했습니다."),
    EMPTY_FILE_UPLOAD(HttpStatus.BAD_REQUEST, "빈 파일은 업로드할 수 없습니다."),

    // 유저 관련
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 공연 관련
    PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공연을 찾을 수 없습니다."),
    PERFORMANCE_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공연의 회차 정보를 찾을 수 없습니다."),
    PERFORMANCE_PENDING_APPROVAL(HttpStatus.BAD_REQUEST, "공연이 승인 대기 중입니다. 승인 후 회차 등록이 가능합니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이미지를 찾을 수 없습니다.");
    // 예약 관련

    // 환불 관련

    // 정산 관련


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public AppException serviceException() {
        return new AppException(this, ErrorType.SERVICE);
    }

    public AppException serviceException(String detail) {
        return new AppException(this, detail, ErrorType.SERVICE);
    }

    public AppException domainException(String detail) {
        return new AppException(this, detail, ErrorType.DOMAIN);
    }

    public AppException persistenceException(String detail) {
        return new AppException(this, detail, ErrorType.PERSISTENCE);
    }
}