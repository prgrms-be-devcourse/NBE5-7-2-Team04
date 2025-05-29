package me.performancereservation.global.config;

import me.performancereservation.global.logtrace.LogTrace;
import me.performancereservation.global.logtrace.LogTraceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }
}
