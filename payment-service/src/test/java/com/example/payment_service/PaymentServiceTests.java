package com.example.payment_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.example.common.CustomException;
import com.example.payment_service.dtos.CourseDTO;
import com.example.payment_service.dtos.TransactionWithUserAndCourseDTO;
import com.example.payment_service.dtos.UserDTO;
import com.example.payment_service.models.PaymentRequest;
import com.example.payment_service.models.Transaction;

class PaymentServiceTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RemoteServiceClient remoteServiceClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentService paymentService;

    private Transaction testTransaction;
    private PaymentRequest testPaymentRequest;
    private UserDTO testUser;
    private CourseDTO testCourse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setUserId(1L);
        testTransaction.setCourseId(1L);
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setStatus("APPROVED");
        testTransaction.setTimestamp(LocalDateTime.now());

        testPaymentRequest = new PaymentRequest();
        testPaymentRequest.setUserId(1L);
        testPaymentRequest.setCourseId(1L);
        testPaymentRequest.setAmount(BigDecimal.valueOf(100));
        testPaymentRequest.setPaymentMethod("CARD");

        testUser = new UserDTO("testUser", "test@test.com");
        testCourse = new CourseDTO("Test Course", "Test Description", 100);
    }

    @Test
    void whenProcessPayment_withValidRequest_thenSuccess() {
        when(paymentRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(null);
        when(remoteServiceClient.getCourseById(1L)).thenReturn(testCourse);
        when(remoteServiceClient.getUserById(1L)).thenReturn(testUser);
        when(paymentRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionWithUserAndCourseDTO result = paymentService.processPayment(testPaymentRequest);

        assertNotNull(result);
        assertEquals("APPROVED", result.getTransaction().getStatus());
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
    }

    @Test
    void whenProcessPayment_withExistingApprovedTransaction_thenThrowException() {
        when(paymentRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(testTransaction);

        assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment(testPaymentRequest);
        });
    }

    @Test
    void whenGetAll_thenReturnListOfTransactions() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(paymentRepository.findAll()).thenReturn(transactions);
        when(remoteServiceClient.getUserById(1L)).thenReturn(testUser);
        when(remoteServiceClient.getCourseById(1L)).thenReturn(testCourse);

        List<TransactionWithUserAndCourseDTO> result = paymentService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTransaction.getId(), result.get(0).getTransaction().getId());
    }

    @Test
    void whenPaymentApprovement_thenReturnTransaction() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(paymentRepository.getByUserId(1L)).thenReturn(transactions);

        Transaction result = paymentService.paymentApprovement(1L, 1L);

        assertNotNull(result);
        assertEquals(testTransaction.getId(), result.getId());
    }

    
    @Test
    void whenGetByIdNotFound_thenThrowException() {
      
        when(paymentRepository.findById(999L)).thenReturn(null);
    
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            paymentService.getById(999L);
        });
    
        assertEquals("No value present", exception.getMessage());
    }
    
    @Test
    void whenUpdatePayment_thenSuccess() {
        when(paymentRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        
        paymentService.updatePayment(testTransaction);
        
        verify(paymentRepository).save(testTransaction);
    }

    @Test
    void whenDeletePayment_thenSuccess() {
        doNothing().when(paymentRepository).deleteById(anyLong());
        
        paymentService.deletePayment(1L);
        
        verify(paymentRepository).deleteById(1L);
    }

    @Test
    void whenProcessPayment_withServiceUnavailable_thenThrowCustomException() {
        when(paymentRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(null);
        when(remoteServiceClient.getCourseById(1L))
            .thenThrow(new CustomException("Service Unavailable", "SERVICE_UNAVAILABLE", 503));

        assertThrows(CustomException.class, () -> {
            paymentService.processPayment(testPaymentRequest);
        });
    }

    @Test
    void whenProcessPayment_withInvalidAmount_thenThrowException() {
        testPaymentRequest.setAmount(BigDecimal.valueOf(50.00));
        when(paymentRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(null);
        when(remoteServiceClient.getCourseById(1L)).thenReturn(testCourse);

        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.processPayment(testPaymentRequest);
        });
    }
}