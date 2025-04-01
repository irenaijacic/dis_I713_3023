package com.example.user_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.user_service.models.User;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseServicProxy courseServicProxy;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setRole("USER");

        secondUser = new User();
        secondUser.setId(2L);
        secondUser.setEmail("test2@test.com");
        secondUser.setUsername("testUser2");
        secondUser.setPassword("password2");
        secondUser.setRole("ADMIN");
    }

    @Test
    void whenCreateUser_thenReturnSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User savedUser = userService.createUser(testUser);

        assertNotNull(savedUser);
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getUsername(), savedUser.getUsername());
        verify(userRepository).save(testUser);
    }

    @Test
    void whenCreateUserWithNullValues_thenReturnSavedUser() {
        User nullUser = new User();
        when(userRepository.save(any(User.class))).thenReturn(nullUser);

        User savedUser = userService.createUser(nullUser);

        assertNotNull(savedUser);
        verify(userRepository).save(nullUser);
    }

    @Test
    void whenGetUserByUsername_thenReturnUser() {
        when(userRepository.findByUsername("testUser")).thenReturn(testUser);

        User found = userService.getUserByUsername("testUser");

        assertNotNull(found);
        assertEquals(testUser.getUsername(), found.getUsername());
        verify(userRepository).findByUsername("testUser");
    }

    @Test
    void whenGetUserByUsername_withNonexistentUsername_thenReturnNull() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        User found = userService.getUserByUsername("nonexistent");

        assertNull(found);
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void whenGetUserById_thenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> found = userService.getUserById(1L);

        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void whenGetUserById_withNonexistentId_thenReturnEmptyOptional() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> found = userService.getUserById(999L);

        assertFalse(found.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    void whenGetAll_thenReturnUserList() {
        List<User> userList = Arrays.asList(testUser, secondUser);
        when(userRepository.findAll()).thenReturn(userList);

        List<User> found = userService.getAll();

        assertNotNull(found);
        assertEquals(2, found.size());
        assertEquals(testUser.getUsername(), found.get(0).getUsername());
        assertEquals(secondUser.getUsername(), found.get(1).getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void whenGetAll_withEmptyDatabase_thenReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<User> found = userService.getAll();

        assertNotNull(found);
        assertTrue(found.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void whenDeleteUser_thenVerifyRepositoryCall() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void whenUpdateUser_thenVerifyRepositoryCall() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.updateUser(testUser);

        verify(userRepository).save(testUser);
    }

    @Test
    void whenUpdateUser_withModifiedData_thenVerifyUpdates() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("updated@test.com");
        updatedUser.setUsername("updatedUser");
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        userService.updateUser(updatedUser);

        verify(userRepository).save(updatedUser);
    }

    @Test
    void whenGetUsersByIds_thenReturnUserList() {
        List<Long> ids = Arrays.asList(1L, 2L);
        List<User> userList = Arrays.asList(testUser, secondUser);
        when(userRepository.findAllById(ids)).thenReturn(userList);

        List<User> found = userService.getUsersByIds(ids);

        assertNotNull(found);
        assertEquals(2, found.size());
        assertEquals(testUser.getId(), found.get(0).getId());
        assertEquals(secondUser.getId(), found.get(1).getId());
        verify(userRepository).findAllById(ids);
    }

    @Test
    void whenGetUsersByIds_withEmptyList_thenReturnEmptyList() {
        List<Long> emptyIds = new ArrayList<>();
        when(userRepository.findAllById(emptyIds)).thenReturn(new ArrayList<>());

        List<User> found = userService.getUsersByIds(emptyIds);

        assertNotNull(found);
        assertTrue(found.isEmpty());
        verify(userRepository).findAllById(emptyIds);
    }

    @Test
    void whenGetCoursesForUser_thenReturnCourseList() {
        List<String> courseNames = Arrays.asList("Course1", "Course2");
        when(courseServicProxy.getCourseNamesByUserId(1L)).thenReturn(courseNames);

        List<String> found = userService.getCoursesForUser(1L);

        assertNotNull(found);
        assertEquals(2, found.size());
        assertEquals("Course1", found.get(0));
        assertEquals("Course2", found.get(1));
        verify(courseServicProxy).getCourseNamesByUserId(1L);
    }


    @Test
    void whenGetCoursesForUser_andServiceFails_thenReturnFallback() {
        when(courseServicProxy.getCourseNamesByUserId(1L))
                .thenThrow(new RuntimeException("Service Unavailable"));

        List<String> fallbackResponse = userService.fallbackForGetCourseNamesByUserId(1L, new RuntimeException());

        assertNotNull(fallbackResponse);
        assertEquals(1, fallbackResponse.size());
        assertEquals("Fallback: Course Service is unavailable", fallbackResponse.get(0));
    }
    
    @Test
    void testFallbackMethod() {
        List<String> fallbackResponse = userService.fallbackForGetCourseNamesByUserId(1L, 
            new RuntimeException("Test Exception"));

        assertNotNull(fallbackResponse);
        assertEquals(1, fallbackResponse.size());
        assertEquals("Fallback: Course Service is unavailable", fallbackResponse.get(0));
    }
    @Test
    void whenGetCoursesForUser_withEmptyResponse_thenReturnNotEnrolledMessage() {
       
        when(courseServicProxy.getCourseNamesByUserId(1L)).thenReturn(new ArrayList<>());
    
       
        List<String> found = userService.getCoursesForUser(1L);
    
      
        System.out.println("Rezultat metode: " + found);
    
      
        assertEquals(1, found.size());
        assertEquals("Korisnik nije upisan ni na jedan kurs.", found.get(0));
    
        verify(courseServicProxy).getCourseNamesByUserId(1L);
    }

}