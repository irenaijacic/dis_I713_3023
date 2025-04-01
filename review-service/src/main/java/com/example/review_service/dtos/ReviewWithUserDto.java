package com.example.review_service.dtos;

import com.example.review_service.models.Review;

public class ReviewWithUserDto {
     private Review review;
    private UserDTO user;
    private CourseDTO course;

    public ReviewWithUserDto(Review review, UserDTO user,CourseDTO course) {
        this.review = review;
        this.user = user;
        this.course = course;
    }

    public Review getReview() {
        return review;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

}
