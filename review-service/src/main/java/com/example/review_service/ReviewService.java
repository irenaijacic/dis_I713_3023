package com.example.review_service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.common.CustomException;
import com.example.review_service.dtos.CourseDTO;
import com.example.review_service.dtos.ReviewWithUserDto;
import com.example.review_service.dtos.UserDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.review_service.models.Review;
import com.example.review_service.security.RemoteServiceClient;


@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserServiceProxy userServiceProxy;

    @Autowired
    private CourseServiceProxy courseServiceProxy;

    @Autowired
    private RemoteServiceClient remoteServiceClient;

    public ReviewWithUserDto createReview(Review review) {
        // UserDTO userDto = userServiceProxy.getUserById(review.getUserId());
        // CourseDTO courseDto = courseServiceProxy.getCourseById(review.getCourseId());
        UserDTO userDto = remoteServiceClient.getUserById(review.getUserId());
        CourseDTO courseDto = remoteServiceClient.getCourseById(review.getCourseId());
        reviewRepository.save(review);
        return new ReviewWithUserDto(review, userDto, courseDto);
    }

    public List<ReviewWithUserDto> getAll() {

        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream().map(review -> {

            // UserDTO userDto = userServiceProxy.getUserById(review.getUserId());
            // CourseDTO courseDto = courseServiceProxy.getCourseById(review.getCourseId());
            UserDTO userDto = remoteServiceClient.getUserById(review.getUserId());
            CourseDTO courseDto = remoteServiceClient.getCourseById(review.getCourseId());

            
            if ("Unknown User".equals(userDto.getUsername())) {
                throw new CustomException("User Service is unavailable for User ID: " + review.getUserId(),
                        "USER_SERVICE_UNAVAILABLE", 503);
            }

            if ("Unknown Course".equals(courseDto.getName())) {
                throw new CustomException("Course Service is unavailable for Course ID: " + review.getCourseId(),
                        "COURSE_SERVICE_UNAVAILABLE", 503);
            }
            return new ReviewWithUserDto(review, userDto, courseDto);
        }).collect(Collectors.toList());
    }

    public ReviewWithUserDto getReviewById(long reviewId) {
        Review review = reviewRepository.findById(reviewId);

        if (review == null) {
            throw new IllegalArgumentException("Review sa ID-jem " + reviewId + " ne postoji!");
        }

        CourseDTO courseDto = remoteServiceClient.getCourseById(review.getCourseId());
        UserDTO userDto = remoteServiceClient.getUserById(review.getUserId());

        return new ReviewWithUserDto(review, userDto, courseDto);
    }

    public Review getReviewById1(long reviewId) {

        Review review = reviewRepository.findById(reviewId);
        ;
        return review;
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public void updateReview(Review review) {
        reviewRepository.save(review);
    }

    public UserDTO getUserAuthorOfReview(Long reviewId) {
        Optional<Review> review1 = reviewRepository.findById(reviewId);

        if (review1.isEmpty()) {
            throw new IllegalArgumentException("Review sa ID-jem " + reviewId + " ne postoji!");
        }
        Review review = review1.get();
        return remoteServiceClient.getUserById(review.getUserId());
    }
}
