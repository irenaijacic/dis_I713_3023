package com.example.api_gateway;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class FallbackController {
    
   
    @GetMapping("fallback/userServiceFallBack")
    public String userServiceFallBack() {
        return "User Service is down!";
    }

    @GetMapping("fallback/courseServiceFallBack")
    public String courseServiceFallBack() {
        return "Course Service is down!";
    }

    @GetMapping("fallback/reviewServiceFallBack")
    public String reviewServiceFallBack() {
        return "Review Service is down!";
    }

    @GetMapping("fallback/paymentServiceFallBack")
    public String paymentServiceFallBack() {
        return "Payment Service is down!";
    }

    @GetMapping("fallback/notificationServiceFallBack")
    public String notificationServiceFallBack() {
        return "Notification Service is down!";
    }

}
