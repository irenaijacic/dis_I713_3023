package com.example.user_service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.common.OAuthFeignConfig;


@FeignClient(name = "course-service", configuration = OAuthFeignConfig.class)
public interface CourseServicProxy {

    @GetMapping("/course-service/courses/{userId}/names")
    List<String> getCourseNamesByUserId(@PathVariable("userId") Long userId);

  

}
