package com.example.api_gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import reactor.core.publisher.Mono;

@Configuration
public class ApiGatewayConfiguration {

        @Bean
        public RouteLocator gateway(RouteLocatorBuilder builder) {
                return builder.routes()
                                
                                .route(p -> p.path("/user-service/**")
                                                .filters(f -> f
                                                                .circuitBreaker(c -> c.setName("user-service")
                                                                                .setFallbackUri("forward:/fallback/userServiceFallBack"))
                                                                .requestRateLimiter(r -> r
                                                                                .setRateLimiter(redisRateLimiter())
                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://user-service"))
                                .route(p -> p.path("/course-service/**")
                                                .filters(f -> f
                                                                .circuitBreaker(c -> c.setName("course-service")
                                                                                .setFallbackUri("forward:/fallback/courseServiceFallBack"))
                                                                .requestRateLimiter(r -> r
                                                                                .setRateLimiter(redisRateLimiter())
                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://course-service"))
                                .route(p -> p.path("/review-service/**")
                                                .filters(f -> f
                                                                .circuitBreaker(c -> c.setName("review-service")
                                                                                .setFallbackUri("forward:/fallback/reviewServiceFallBack"))
                                                                .requestRateLimiter(r -> r
                                                                                .setRateLimiter(redisRateLimiter())
                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://review-service"))
                                .route(p -> p.path("/payment-service/**")
                                                .filters(f -> f
                                                                .circuitBreaker(c -> c.setName("payment-service")
                                                                                .setFallbackUri("forward:/fallback/paymentServiceFallBack"))
                                                                .requestRateLimiter(r -> r
                                                                                .setRateLimiter(redisRateLimiter())
                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://payment-service"))
                                .route(p -> p.path("/notification-service/**")
                                                .filters(f -> f
                                                                .circuitBreaker(c -> c.setName("notification-service")
                                                                                .setFallbackUri("forward:/fallback/notificationServiceFallBack"))
                                                                .requestRateLimiter(r -> r
                                                                                .setRateLimiter(redisRateLimiter())
                                                                                .setKeyResolver(userKeyResolver())))
                                                .uri("lb://notification-service"))
                                .build();
        }

        @Bean
        public org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter redisRateLimiter() {
                return new org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter(10, 20);
        }

        @Bean
        public KeyResolver userKeyResolver() {
                return exchange -> Mono.just("userKey");
        }

}
