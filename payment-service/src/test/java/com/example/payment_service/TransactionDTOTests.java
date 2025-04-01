package com.example.payment_service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.payment_service.dtos.TransactionDTO;

class TransactionDTOTests {

    private TransactionDTO transactionDTO;
    private LocalDateTime timestamp;

    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO();
        timestamp = LocalDateTime.now();
    }

    @Test
    void testSetAndGetId() {
        Long id = 1L;
        transactionDTO.setId(id);
        assertEquals(id, transactionDTO.getId());
    }

    @Test
    void testSetAndGetUserId() {
        Long userId = 2L;
        transactionDTO.setUserId(userId);
        assertEquals(userId, transactionDTO.getUserId());
    }

    @Test
    void testSetAndGetCourseId() {
        Long courseId = 3L;
        transactionDTO.setCourseId(courseId);
        assertEquals(courseId, transactionDTO.getCourseId());
    }

    @Test
    void testSetAndGetAmount() {
        BigDecimal amount = new BigDecimal("100");
        transactionDTO.setAmount(amount);
        assertEquals(amount, transactionDTO.getAmount());
    }

    @Test
    void testSetAndGetStatus() {
        String status = "APPROVED";
        transactionDTO.setStatus(status);
        assertEquals(status, transactionDTO.getStatus());
    }

    @Test
    void testSetAndGetTimestamp() {
        transactionDTO.setTimestamp(timestamp);
        assertEquals(timestamp, transactionDTO.getTimestamp());
    }

    @Test
    void testAllFieldsSetAndGet() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        Long courseId = 3L;
        BigDecimal amount = new BigDecimal("100");
        String status = "APPROVED";

        // Act
        transactionDTO.setId(id);
        transactionDTO.setUserId(userId);
        transactionDTO.setCourseId(courseId);
        transactionDTO.setAmount(amount);
        transactionDTO.setStatus(status);
        transactionDTO.setTimestamp(timestamp);

        // Assert
        assertEquals(id, transactionDTO.getId());
        assertEquals(userId, transactionDTO.getUserId());
        assertEquals(courseId, transactionDTO.getCourseId());
        assertEquals(amount, transactionDTO.getAmount());
        assertEquals(status, transactionDTO.getStatus());
        assertEquals(timestamp, transactionDTO.getTimestamp());
    }

    @Test
    void testNullValues() {
        assertNull(transactionDTO.getId());
        assertNull(transactionDTO.getUserId());
        assertNull(transactionDTO.getCourseId());
        assertNull(transactionDTO.getAmount());
        assertNull(transactionDTO.getStatus());
        assertNull(transactionDTO.getTimestamp());
    }
}