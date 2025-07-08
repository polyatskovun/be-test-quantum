package it.polyatskovun.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class LoggingAspect {

    private final ObjectMapper objectMapper;

    @Around("@annotation(it.polyatskovun.aop.LogMethod)")
    public Object logHttpRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpServletResponse httpResponse =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        String requestBody = getRequestBody(joinPoint);
        log.info("Request: Method: {}, URI: {}, Body: {}",
                request.getMethod(),
                request.getRequestURI(),
                requestBody
        );
        Object response = null;
        long startTime = System.currentTimeMillis();
        long endTime;
        try {
            response = joinPoint.proceed();
            endTime = System.currentTimeMillis();
        } catch (Throwable e) {
            log.error("Exception: Method: {}, URI: {} failed with exception message: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    e.getMessage()
            );
            throw e;
        }
        long duration = endTime - startTime;
        String responseBody = convertObjectToJson(response);
        log.info("Response: Method: {}, URI: {}, Status {} - Body: {} Time Taken: {} ms",
                request.getMethod(),
                request.getRequestURI(),
                httpResponse != null ? httpResponse.getStatus() : 0,
                responseBody,
                duration
        );
        return response;
    }

    private String getRequestBody(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            try {
                return Arrays.stream(args)
                        .map(this::convertObjectToJson)
                        .reduce((arg1, arg2) -> arg1 + ", " + arg2)
                        .orElse("");
            } catch (Exception e) {
                log.error("Error serializing request body", e);
            }
        }
        return "";
    }

    private String convertObjectToJson(Object object) {
        if (object == null)
            return "";
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error serializing object to JSON", e);
            return "Error serializing object to JSON";
        }
    }
}
