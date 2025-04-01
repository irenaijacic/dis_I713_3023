package com.example.review_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.common.CustomException;
import com.example.review_service.models.Review;
import com.example.review_service.security.RemoteServiceClient;
import com.example.review_service.dtos.ReviewWithUserDto;
import com.example.review_service.dtos.UserDTO;
import com.example.review_service.dtos.CourseDTO;

class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserServiceProxy userServiceProxy;

    @Mock
    private CourseServiceProxy courseServiceProxy;

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private RemoteServiceClient remoteServiceClient; 

    private Review testReview;
    private UserDTO testUser;
    private CourseDTO testCourse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testReview = new Review();
        testReview.setId(1L);
        testReview.setUserId(1L);
        testReview.setCourseId(1L);
        testReview.setRating(4);
        testReview.setComment("Test komentar");
        testReview.setCreatedAt(new Date());

        testUser = new UserDTO("testUser", "test@test.com");
        testCourse = new CourseDTO("Test Course", "Test Description", 99.99f);

       // when(userServiceProxy.getUserById(1L)).thenReturn(testUser);
       // when(courseServiceProxy.getCourseById(1L)).thenReturn(testCourse);
       when(remoteServiceClient.getUserById(1L)).thenReturn(testUser);
       when(remoteServiceClient.getCourseById(1L)).thenReturn(testCourse);
    }

    @Test
    void whenCreateReview_thenReturnReviewWithUserDto() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        ReviewWithUserDto result = reviewService.createReview(testReview);

        assertNotNull(result);
        assertEquals(testReview.getComment(), result.getReview().getComment());
        assertEquals(testUser.getUsername(), result.getUser().getUsername());
        assertEquals(testCourse.getName(), result.getCourse().getName());
    }

    @Test
    void whenGetAll_thenReturnListOfReviewWithUserDto() {
        when(reviewRepository.findAll()).thenReturn(Arrays.asList(testReview));

        List<ReviewWithUserDto> result = reviewService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReview.getComment(), result.get(0).getReview().getComment());
    }

    @Test
    void whenGetReviewById_thenReturnReviewWithUserDto() {
        when(reviewRepository.findById(1L)).thenReturn(testReview);

        ReviewWithUserDto result = reviewService.getReviewById(1L);

        assertNotNull(result);
        assertEquals(testReview.getComment(), result.getReview().getComment());
        assertEquals(testUser.getUsername(), result.getUser().getUsername());
    }

    @Test
    void whenGetReviewById1_thenReturnReview() {
        when(reviewRepository.findById(1L)).thenReturn(testReview);

        Review result = reviewService.getReviewById1(1L);

        assertNotNull(result);
        assertEquals(testReview.getComment(), result.getComment());
    }

    @Test
    void whenDeleteReview_thenVerifyRepositoryCall() {
        doNothing().when(reviewRepository).deleteById(1L);
        reviewService.deleteReview(1L);
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    void whenUpdateReview_thenVerifyRepositorySave() {
        Review updatedReview = new Review();
        updatedReview.setId(1L);
        updatedReview.setRating(5);
        updatedReview.setComment("Updated comment");
        
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);
        
        reviewService.updateReview(updatedReview);
        
        verify(reviewRepository).save(updatedReview);
    }

    @Test
    void whenGetReviewById_AndReviewNotFound_thenThrowException() {
        when(reviewRepository.findById(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.getReviewById(999L);
        });
    }

   

}