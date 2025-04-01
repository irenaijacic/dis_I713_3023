package com.example.course_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.common.OAuthFeignConfig;


@FeignClient(name = "payment-service", configuration = OAuthFeignConfig.class)
public interface PaymentServiceProxy {

    @GetMapping("/payment-service/payments/forCourse")
    public Boolean isTransactionApproved(@RequestParam Long userId, @RequestParam Long courseId,
            @RequestParam float price);

}
