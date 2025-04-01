package com.example.course_service;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.course_service.models.Course;
import com.example.course_service.dtos.CourseWithUsersDto;
import com.example.course_service.dtos.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CourseController.class)
class CourseControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private PaymentServiceProxy paymentServiceProxy;

    @MockBean
    private RemoteServiceClient remoteServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Course testCourse;
    private CourseWithUsersDto courseWithUsersDto;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setName("Test Course");
        testCourse.setDescription("Test Description");
        testCourse.setPrice(99.99f);
        testCourse.setCategory("Test Category");
        testCourse.setDuration(30);

        UserDto testUser = new UserDto();
        testUser.setUsername("testUser");
        testUser.setEmail("test@test.com");

        courseWithUsersDto = new CourseWithUsersDto(
            1L, "Test Course", "Test Description", 99.99f,
            Arrays.asList(testUser), "Test Category", 30
        );
    }

    @Test
    @WithMockUser(authorities = "admin")
    void whenGetAllCourses_thenReturnJsonArray() throws Exception {
        when(courseService.getAll()).thenReturn(Arrays.asList(testCourse));

        mockMvc.perform(get("/courses")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Course"));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void whenGetCourseById_thenReturnCourse() throws Exception {
        when(courseService.getCourseById1(1L)).thenReturn(courseWithUsersDto);

        mockMvc.perform(get("/courses/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Course"));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void whenCreateCourse_thenReturnCreatedCourse() throws Exception {
        when(courseService.createCourse(any(Course.class))).thenReturn(testCourse);

        mockMvc.perform(post("/courses")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCourse)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void whenEnrollUser_thenReturnSuccess() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(Optional.of(testCourse));
        when(remoteServiceClient.isTransactionApproved(1L, 1L, 99.99f)).thenReturn(true);

        mockMvc.perform(post("/courses/1/enroll/1")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"admin", "SCOPE_internal"})
    void whenGetCourseNamesByUserId_thenReturnList() throws Exception {
        when(courseService.getCourseNamesByUserId(1L))
                .thenReturn(Arrays.asList("Course1", "Course2"));

        mockMvc.perform(get("/courses/1/names")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Course1"));
    }

        @Test
    @WithMockUser(authorities = "admin")
    void whenGetCourseById_NotFound_thenReturn404() throws Exception {
        when(courseService.getCourseById1(99L)).thenThrow(new IllegalArgumentException("Course not found"));

        mockMvc.perform(get("/courses/99")
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));
    }

            @Test
        @WithMockUser(authorities = "admin")
        void whenEnrollUser_TransactionNotApproved_thenReturn400() throws Exception {
            when(courseService.getCourseById(1L)).thenReturn(Optional.of(testCourse));
            when(remoteServiceClient.isTransactionApproved(1L, 1L, 99.99f)).thenReturn(false);

            mockMvc.perform(post("/courses/1/enroll/1")
                    .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Nemas pravo na upis"));
        }

    
}