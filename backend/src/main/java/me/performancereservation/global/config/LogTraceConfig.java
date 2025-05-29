package me.performancereservation.global.config;

import me.performancereservation.global.logtrace.LogTrace;
import me.performancereservation.global.logtrace.ThreadLocalLogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogTraceConfig {

    @Bean
    public LogTrace logTrace() { return new ThreadLocalLogTrace();}
}
