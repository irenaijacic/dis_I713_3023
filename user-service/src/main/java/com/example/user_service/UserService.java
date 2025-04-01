package com.example.user_service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.models.User;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseServicProxy courseServicProxy;

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
  
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public List<User> getUsersByIds(List<Long> ids) {

        List<User> users = userRepository.findAllById(ids);

        return users;
    }

    @CircuitBreaker(name = "courseService", fallbackMethod = "fallbackForGetCourseNamesByUserId")
    public List<String> getCoursesForUser(Long userId) {
        try {
            List<String> courses = courseServicProxy.getCourseNamesByUserId(userId);

            if (courses != null && courses.isEmpty()) {
                return List.of("Korisnik nije upisan ni na jedan kurs.");
            }

            return courses;
        } catch (FeignException.NotFound e) {  // Ako je 404, NE aktiviramo Circuit Breaker, već vraćamo poruku
            return List.of("Korisnik nije upisan ni na jedan kurs.");
        } 
    }

  

    public List<String> fallbackForGetCourseNamesByUserId(Long userId, Throwable throwable) {
        System.out.println("Circuit Breaker activated! Returning fallback response.");
        return List.of("Fallback: Course Service is unavailable");
    }
}
