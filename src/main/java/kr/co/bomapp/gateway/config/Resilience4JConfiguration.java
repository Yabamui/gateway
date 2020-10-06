package kr.co.bomapp.gateway.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class Resilience4JConfiguration {
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
    }

    CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            // 통신 실패율- 해당 값보다 크면 개방 default 50
            .failureRateThreshold(50)
            // slow 통신율 default 100
            .slowCallRateThreshold(50)
            // slow 통신 시간 default 60000
            .slowCallDurationThreshold(Duration.ofMillis(60000))
            // circuit-breaker half-open 상태에 허용 호출 수 default 10
            .permittedNumberOfCallsInHalfOpenState(10)
            // circuit-breaker 가 실패/slow 판단을 위한 최소 통신 수 10 미만 시 circuit-breaker 는 open 되지 않음 default 10
            .minimumNumberOfCalls(5)
            // circuit-breaker 가 close 되는 기반 COUNT_BASED slidingWindowSize 호출 기록 집계 / TIME_BASED slidingWindowSize 초 기록 집계
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            // circuit-breaker 가 close 되는 SlidingWindowType 의 기록 수
            .slidingWindowSize(5)
            // 실패로 기록되어 실패율을 증가시키는 예외 목록
            .recordExceptions(IOException.class, TimeoutException.class)
            // 실패 또는 성공으로 간주되지 않는 예외 목록
            .ignoreExceptions()
            .build();

    TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
            .cancelRunningFuture(true)
            .timeoutDuration(Duration.ofSeconds(5))
            .build();

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getURI().getPath());
    }

}
