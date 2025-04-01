package com.example.review_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.review_service.models.*;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

     Review findById(long id);
}
