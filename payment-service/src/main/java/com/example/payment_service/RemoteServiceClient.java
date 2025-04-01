package com.example.payment_service;

import org.springframework.stereotype.Service;

import com.example.common.CustomException;
import com.example.payment_service.dtos.CourseDTO;
import com.example.payment_service.dtos.UserDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class RemoteServiceClient {

    private final UserServiceProxy userServiceProxy;
    private final CourseServiceProxy courseServiceProxy;

    public RemoteServiceClient(UserServiceProxy userServiceProxy, CourseServiceProxy courseServiceProxy) {
        this.userServiceProxy = userServiceProxy;
        this.courseServiceProxy = courseServiceProxy;
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "fallbackForUser")
    public UserDTO getUserById(Long userId) {
        return userServiceProxy.getUserById(userId);
    }

    @CircuitBreaker(name = "course-service", fallbackMethod = "fallbackForCourse")
    public CourseDTO getCourseById(Long courseId) {
       CourseDTO course =  courseServiceProxy.getCourseById(courseId);
       if(course == null){
        throw new IllegalArgumentException("Kurs ne postoji!");
       }
       return course;
    }

    public UserDTO fallbackForUser(Long userId, Throwable t) {
        throw new CustomException("User Service is currently unavailable",
                "USER_SERVICE_UNAVAILABLE", 503);
       
    }

    public CourseDTO fallbackForCourse(Long courseId, Throwable t) {
        throw new CustomException("Course Service is currently unavailable",
                "COURSE_SERVICE_UNAVAILABLE", 503);
    }
        
}