package com.example.course_service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.course_service.dtos.UserDto;
import com.example.course_service.models.Course;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.example.course_service.dtos.CourseWithUsersDto;
import com.example.course_service.RemoteServiceClient;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserServiceProxy userServiceProxy;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RemoteServiceClient remoteServiceClient;

    public Course createCourse(Course course) {

        return courseRepository.save(course);
    }

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public CourseWithUsersDto getCourseById1(Long id) {
       Course course = courseRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Kurs sa ID " + id + " ne psotoji."));

        List<Long> enrolledUserIds = course.getEnrolledUserIds();
        String idsAsString = enrolledUserIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        List<UserDto> users = remoteServiceClient.getUsersByIds(idsAsString);

        return new CourseWithUsersDto(course.getId(), course.getName(), course.getDescription(),
                course.getPrice(), users, course.getCategory(), course.getDuration());

    }

    public Course getCourseByName(String name){
        return courseRepository.getByName(name);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    public CourseWithUsersDto getCourseWithUsers(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        Course course1 = course.get();
        List<Long> enrolledUserIds = course1.getEnrolledUserIds();
        String idsAsString = enrolledUserIds.stream()
                .map(String::valueOf) 
                .collect(Collectors.joining(","));

        List<UserDto> users = remoteServiceClient.getUsersByIds(idsAsString);

        return new CourseWithUsersDto(course1.getId(), course1.getName(), course1.getDescription(), course1.getPrice(),
                users, course1.getCategory(), course1.getDuration());
    }

    public List<Course> searchCourses(String name, String category) {
        return courseRepository.searchCourses(name, category);
    }

    public List<String> getCourseNamesByUserId(Long userId) {
        return courseRepository.findCourseNamesByUserId(userId);
    }

    public void enrollUserToCourse(Long courseId, Long userId){
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Nema ovog kursa"));

        if (course.getEnrolledUserIds().contains(userId)) {
            throw new RuntimeException("Ovaj korisnik je vec upisan na kurs");
        }
       
        course.getEnrolledUserIds().add(userId);
        try {
            String message = String.format(
                    "Successfull enrollment for user %s on course %s",
                    userId,
                   courseId);
            rabbitTemplate.convertAndSend("notification_course_queue_exchange","notification_course_key",message);
            System.out.println("Notification sent to RabbitMQ: " + message);
            System.out.println("Message sent to queue: " + message);
        } catch (Exception e) {
            System.err.println("Failed to send notification to RabbitMQ: " + e.getMessage());
        }

        courseRepository.save(course);
    }

  

}
