package com.example.course_service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.course_service.dtos.UserDto;

import com.example.common.OAuthFeignConfig;


@FeignClient(name = "user-service", configuration = OAuthFeignConfig.class)
public interface UserServiceProxy {

  @GetMapping("/users/{id}")
  UserDto getUserById(@PathVariable Long id);

  @GetMapping("/user-service/users/forCourse")
  List<UserDto> getUsersByIds(@RequestParam("ids") String ids);
 

}
