package com.example.course_service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.example.common.CustomException;
import com.example.course_service.PaymentServiceProxy;
import com.example.course_service.UserServiceProxy;
import com.example.course_service.dtos.CourseDto;
import com.example.course_service.dtos.UserDto;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class RemoteServiceClient {

    private final UserServiceProxy userServiceProxy;
    private final PaymentServiceProxy paymentServiceProxy;

    public RemoteServiceClient(UserServiceProxy userServiceProxy, PaymentServiceProxy paymentServiceProxy) {
        this.userServiceProxy = userServiceProxy;
        this.paymentServiceProxy = paymentServiceProxy;
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "fallbackForUser")
    public UserDto getUserById(Long userId) {
        return userServiceProxy.getUserById(userId);
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "fallbackForUser1")
    public List<UserDto> getUsersByIds(String ids) {
        return userServiceProxy.getUsersByIds(ids);
    }

    @CircuitBreaker(name = "payment-service", fallbackMethod = "fallbackForPayment")
    public Boolean isTransactionApproved(Long userId, Long courseId,float price) {
        return paymentServiceProxy.isTransactionApproved(userId,courseId,price);
    }

    public UserDto fallbackForUser(Long userId, Throwable t) {
        throw new CustomException("User Service is currently unavailable",
                "USER_SERVICE_UNAVAILABLE", 503);
       
    }

    public List<UserDto> fallbackForUser1(String ids, Throwable t) {
        throw new CustomException("User Service is currently unavailable",
                "USER_SERVICE_UNAVAILABLE", 503);
       
    }

    public Boolean fallbackForPayment(Long userId, Long courseId, float price, Throwable t) {
        throw new CustomException("Payment Service is currently unavailable", "PAYMENT_SERVICE_UNAVAILABLE", 503);
    }
        
}