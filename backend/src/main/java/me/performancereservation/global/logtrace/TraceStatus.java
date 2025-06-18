package me.performancereservation.global.logtrace;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TraceStatus {
    private final TraceId traceId;
    private final Long startTimeMs;
    private final String message;
}
