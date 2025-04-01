package com.example.payment_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.common.OAuthFeignConfig;
import com.example.payment_service.dtos.UserDTO;


@FeignClient(name = "user-service", configuration = OAuthFeignConfig.class)
public interface UserServiceProxy {

    @GetMapping("/user-service/users/forReview")
    UserDTO getUserById( @RequestParam("id")  long id);

  
}

