package com.example.user_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.example.user_service.models.User;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private CourseServicProxy courseServicProxy;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Čistimo bazu pre svakog testa
        userRepository.deleteAll();

        // Kreiramo test korisnika
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("password");
        testUser.setRole("USER");
        
        // Čuvamo korisnika u bazi
        testUser = userRepository.save(testUser);
    }

    @Test
    void testUserAndCourseIntegration() {
        // 1. Prvo proveravamo da li je korisnik uspešno sačuvan
        Optional<User> savedUser = userRepository.findById(testUser.getId());
        assertTrue(savedUser.isPresent());
        assertEquals(testUser.getUsername(), savedUser.get().getUsername());

        // 2. Simuliramo uspešan poziv course servisa
        List<String> mockCourses = List.of("Java 101", "Spring Boot");
        when(courseServicProxy.getCourseNamesByUserId(testUser.getId()))
                .thenReturn(mockCourses);

        // 3. Dobavljamo kurseve za korisnika
        List<String> userCourses = userService.getCoursesForUser(testUser.getId());
        assertEquals(2, userCourses.size());
        assertTrue(userCourses.containsAll(mockCourses));

        // 4. Simuliramo pad course servisa
        when(courseServicProxy.getCourseNamesByUserId(testUser.getId()))
                .thenThrow(new RuntimeException("Service Unavailable"));

        // 5. Proveravamo Circuit Breaker i fallback
        List<String> fallbackCourses = userService.getCoursesForUser(testUser.getId());
        assertEquals(1, fallbackCourses.size());
        assertEquals("Fallback: Course Service is unavailable", fallbackCourses.get(0));

        // 6. Ažuriramo korisnika
        testUser.setEmail("updated@test.com");
        userService.updateUser(testUser);

        // 7. Proveravamo da li je ažuriranje uspešno
        User updatedUser = userRepository.findByUsername("testUser");
        assertEquals("updated@test.com", updatedUser.getEmail());

        // 8. Brišemo korisnika
        userService.deleteUser(testUser.getId());

        // 9. Proveravamo da li je brisanje uspešno
        Optional<User> deletedUser = userRepository.findById(testUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testMultipleUserOperations() {
        // 1. Kreiramo više korisnika
        User secondUser = new User();
        secondUser.setUsername("testUser2");
        secondUser.setEmail("test2@test.com");
        secondUser.setPassword("password2");
        secondUser.setRole("USER");
        userRepository.save(secondUser);

        // 2. Proveravamo listu svih korisnika
        List<User> allUsers = userService.getAll();
        assertEquals(2, allUsers.size());

        // 3. Proveravamo dobavljanje korisnika po ID-ovima
        List<Long> userIds = List.of(testUser.getId(), secondUser.getId());
        List<User> foundUsers = userService.getUsersByIds(userIds);
        assertEquals(2, foundUsers.size());

        // 4. Simuliramo različite odgovore course servisa za različite korisnike
        when(courseServicProxy.getCourseNamesByUserId(testUser.getId()))
                .thenReturn(List.of("Course 1", "Course 2"));
        when(courseServicProxy.getCourseNamesByUserId(secondUser.getId()))
                .thenReturn(List.of("Course 3"));

        // 5. Proveravamo kurseve za oba korisnika
        List<String> user1Courses = userService.getCoursesForUser(testUser.getId());
        List<String> user2Courses = userService.getCoursesForUser(secondUser.getId());
        
        assertEquals(2, user1Courses.size());
        assertEquals(1, user2Courses.size());
    }
}