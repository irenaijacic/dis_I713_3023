package com.example.review_service;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Date;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.example.review_service.models.Review;
import com.example.review_service.dtos.ReviewWithUserDto;
import com.example.review_service.dtos.UserDTO;
import com.example.review_service.dtos.CourseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ReviewController.class)
class ReviewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private Review testReview;
    private ReviewWithUserDto reviewWithUserDto;

    @BeforeEach
    void setUp() {
        testReview = new Review();
        testReview.setId(1L);
        testReview.setUserId(1L);
        testReview.setCourseId(1L);
        testReview.setRating(4);
        testReview.setComment("Test komentar");
        testReview.setCreatedAt(new Date());

        UserDTO testUser = new UserDTO("testUser", "test@test.com");
        CourseDTO testCourse = new CourseDTO("Test Course", "Test Description", 99.99f);
        
        reviewWithUserDto = new ReviewWithUserDto(testReview, testUser, testCourse);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void whenGetAllReviews_thenReturnJsonArray() throws Exception {
        when(reviewService.getAll()).thenReturn(Arrays.asList(reviewWithUserDto));

        mockMvc.perform(get("/reviews")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].review.comment").value("Test komentar"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void whenGetReviewById_thenReturnReview() throws Exception {
        when(reviewService.getReviewById(1L)).thenReturn(reviewWithUserDto);

        mockMvc.perform(get("/reviews/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.review.comment").value("Test komentar"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void whenCreateReview_thenReturnCreatedReview() throws Exception {
        when(reviewService.createReview(any(Review.class))).thenReturn(reviewWithUserDto);

        mockMvc.perform(post("/reviews")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReview)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenDeleteReview_thenReturn200() throws Exception {
        when(reviewService.getReviewById(1L)).thenReturn(reviewWithUserDto);
        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/reviews/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = {"user"}) // authorities mora odgovarati @PreAuthorize
    void whenUpdateReview_thenReturnUpdatedReview() throws Exception {
        // Postavite review
        Review testReview = new Review();
        testReview.setId(1L);
        testReview.setUserId(1L);
    
        // Mockujte servis
        when(reviewService.getReviewById1(1L)).thenReturn(testReview);
        UserDTO author = new UserDTO("author", "user@test.com");
        when(reviewService.getUserAuthorOfReview(1L)).thenReturn(author);
        doNothing().when(reviewService).updateReview(any(Review.class));
    
        // Izvršite zahtev
        mockMvc.perform(put("/reviews/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReview)))
                .andExpect(status().isOk());
    }
}