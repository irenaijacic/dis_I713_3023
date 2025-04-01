package com.example.review_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.common.OAuthFeignConfig;
import com.example.review_service.dtos.CourseDTO;


@FeignClient(name = "course-service",configuration = OAuthFeignConfig.class)
public interface CourseServiceProxy {

    @GetMapping("/course-service/courses/forReview")
	public CourseDTO getCourseById( @RequestParam("id") long id);

    

}