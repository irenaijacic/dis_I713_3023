package com.example.course_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.example.course_service.models.Course;
import com.example.course_service.dtos.UserDto;
import com.example.course_service.dtos.CourseWithUsersDto;

class CourseServiceTests {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserServiceProxy userServiceProxy;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RemoteServiceClient remoteServiceClient;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private UserDto testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setName("Test Course");
        testCourse.setDescription("Test Description");
        testCourse.setPrice(99.99f);
        testCourse.setCategory("Test Category");
        testCourse.setDuration(30);
        testCourse.setEnrolledUserIds(new ArrayList<>(Arrays.asList(1L, 2L)));

        testUser = new UserDto();
        testUser.setUsername("testUser");
        testUser.setEmail("test@test.com");

        // Osnovni mock za repository
        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);
    }

    @Test
    void whenCreateCourse_thenReturnSavedCourse() {
        Course created = courseService.createCourse(testCourse);

        assertNotNull(created);
        assertEquals(testCourse.getName(), created.getName());
        verify(courseRepository).save(testCourse);
    }

    @Test
    void whenGetAllCourses_thenReturnCourseList() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(testCourse));

        List<Course> found = courseService.getAll();

        assertNotNull(found);
        assertEquals(1, found.size());
        verify(courseRepository).findAll();
    }

    @Test
    void whenGetCourseById_thenReturnCourse() {
        // Mocking the repository to return Optional<Course>
        when(courseRepository.findById(any(Long.class))).thenReturn(Optional.of(testCourse));
    
        Optional<Course> found = courseService.getCourseById(1L);
    
        assertTrue(found.isPresent());
        assertEquals(testCourse.getName(), found.get().getName());
    }

    @Test
    void whenGetCourseById1_thenReturnCourseWithUsersDto() {
        when(remoteServiceClient.getUsersByIds(anyString()))
                .thenReturn(Arrays.asList(testUser));

        CourseWithUsersDto dto = courseService.getCourseById1(1L);

        assertNotNull(dto);
        assertEquals(testCourse.getName(), dto.getName());
        assertEquals(1, dto.getEnrolledUsers().size());
    }

    @Test
    void whenGetCourseWithUsers_thenReturnCourseWithUsersDto() {
        when(remoteServiceClient.getUsersByIds(anyString()))
                .thenReturn(Arrays.asList(testUser));

        CourseWithUsersDto dto = courseService.getCourseWithUsers(1L);

        assertNotNull(dto);
        assertEquals(testCourse.getName(), dto.getName());
        assertEquals(1, dto.getEnrolledUsers().size());
    }


    @Test
    void whenSearchCourses_thenReturnFilteredList() {
        when(courseRepository.searchCourses("Test", "Category"))
                .thenReturn(Arrays.asList(testCourse));

        List<Course> found = courseService.searchCourses("Test", "Category");

        assertNotNull(found);
        assertEquals(1, found.size());
        verify(courseRepository).searchCourses("Test", "Category");
    }

    @Test
    void whenGetCourseByName_thenReturnCourse() {
        when(courseRepository.getByName("Test Course")).thenReturn(testCourse);

        Course found = courseService.getCourseByName("Test Course");

        assertNotNull(found);
        assertEquals(testCourse.getName(), found.getName());
        verify(courseRepository).getByName("Test Course");
    }
    @Test
    void whenDeleteCourse_thenVerifyRepositoryCalled() {
        Long courseId = 1L;

        courseService.deleteCourse(courseId);

        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    void whenEnrollUserAlreadyEnrolled_thenThrowException() {
        Long existingUserId = 1L;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            courseService.enrollUserToCourse(1L, existingUserId);
        });

        assertEquals("Ovaj korisnik je vec upisan na kurs", exception.getMessage());
    }
    @Test
    void whenEnrollUserSuccessfully_thenUserAddedAndNotificationSent() {
        Long newUserId = 3L;
        
        doReturn(Optional.of(testCourse)).when(courseRepository).findById(1L);

        courseService.enrollUserToCourse(1L, newUserId);

        assertTrue(testCourse.getEnrolledUserIds().contains(newUserId));
        verify(courseRepository, times(1)).save(testCourse);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("notification_course_queue_exchange"),
                eq("notification_course_key"),
                contains("Successfull enrollment for user")
        );
    }

  
    @Test
    void whenUpdateNonExistingCourse_thenReturnNull() {
        when(courseRepository.save(any(Course.class))).thenReturn(null);

        Course updated = courseService.updateCourse(new Course());

        assertNull(updated);
    }

    @Test
    void whenGetCourseNamesByUserId_NoCourses_thenReturnEmptyList() {
        when(courseRepository.findCourseNamesByUserId(99L)).thenReturn(new ArrayList<>());

        List<String> courseNames = courseService.getCourseNamesByUserId(99L);

        assertNotNull(courseNames);
        assertTrue(courseNames.isEmpty());
    }

}