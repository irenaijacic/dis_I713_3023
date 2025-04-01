package com.example.payment_service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.payment_service.models.Transaction;
@Repository
public interface PaymentRepository extends JpaRepository<Transaction, Long> {

    Transaction findById(long id);

    List<Transaction> findAll();

    List<Transaction> getByUserId(Long userId);

    Transaction findByUserIdAndCourseId(Long userId, Long courseId);


}
