package me.performancereservation.global.logtrace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.performancereservation.domain.user.entitiy.User;
import me.performancereservation.global.security.oauth.user.CustomOAuth2User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class LogTraceAspect {

    private final LogTrace logTrace;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* me.performancereservation.domain..*(..)) " +
            "|| execution(* me.performancereservation.api..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        try {
            // 기본 info 로그
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            // 메서드 정보는 debug 레벨로 출력
            String methodInfo = getMethodInfo(joinPoint);
            log.debug("[{}] Parameters: {}", status.getTraceId().getId(), methodInfo);

            //로직 호출
            Object result = joinPoint.proceed();

            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    private String getMethodInfo(ProceedingJoinPoint joinPoint) {
        // 메서드 시그니처와 파라미터 정보 조합
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.toShortString();
        String params = getParams(signature, joinPoint.getArgs());

        return methodName + " " + params;
    }

    private String getParams(MethodSignature signature, Object[] args) {
        // 파라미터 이름과 값을 매핑
        String[] paramNames = signature.getParameterNames();
        List<String> paramList = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String name = (paramNames != null && i < paramNames.length)
                    ? paramNames[i]
                    : "arg" + i;
            String value = convertToString(args[i]);
            paramList.add(name + "=" + value);
        }

        return "[" + String.join(", ", paramList) + "]";
    }

    private String convertToString(Object arg) {
        try {
            // CustomOAuth2User 처리
            if (arg instanceof CustomOAuth2User) {
                return maskSensitiveInfo((CustomOAuth2User) arg);
            }
            // 다른 민감한 객체 처리 (예: User 엔티티)
            if (arg instanceof User) {
                return maskUserInfo((User) arg);
            }
            return objectMapper.writeValueAsString(arg);
        } catch (JsonProcessingException e) {
            return arg != null ? arg.toString() : "null";
        }
    }

    private String maskSensitiveInfo(CustomOAuth2User oauthUser) {
        // 민감 정보 마스킹
        return String.format(
                "CustomOAuth2User{id=%s, name=%s, role=%s}",
                oauthUser.getUser().getId(),
                oauthUser.getUser().getName(),
                oauthUser.getUser().getRole()
        );
    }

    private String maskUserInfo(User user) {
        // User 엔티티의 민감 정보 제거
        return String.format(
                "User{id=%s, name=%s, role=%s}",
                user.getId(),
                user.getName(),
                user.getRole()
        );
    }
}