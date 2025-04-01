package com.example.payment_service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.CustomException;
import com.example.payment_service.dtos.CourseDTO;
import com.example.payment_service.dtos.TransactionWithUserAndCourseDTO;
import com.example.payment_service.dtos.UserDTO;
import com.example.payment_service.models.PaymentRequest;
import com.example.payment_service.models.Transaction;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserServiceProxy userServiceProxy;

    @Autowired
    CourseServiceProxy courseServiceProxy;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired  
    private RemoteServiceClient remoteServiceClient;

    @Transactional
    public TransactionWithUserAndCourseDTO processPayment(PaymentRequest request) {
       
        Transaction existingTransaction = paymentRepository.findByUserIdAndCourseId(request.getUserId(), request.getCourseId());
        if (existingTransaction != null && "APPROVED".equals(existingTransaction.getStatus())) {
            throw new IllegalStateException("Korisnik je već platio ovaj kurs.");
        }
    
        
        CourseDTO courseDto = remoteServiceClient.getCourseById(request.getCourseId());
        if (courseDto == null || courseDto.getName() == null || courseDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Kurs ne postoji!");
        }
    
      
        if (request.getAmount().compareTo(BigDecimal.valueOf(courseDto.getPrice())) < 0) {
            throw new IllegalArgumentException("Iznos uplate (" + request.getAmount() + ") nije dovoljan za plaćanje kursa. Cena kursa je " + courseDto.getPrice());
        } else if (request.getAmount().compareTo(BigDecimal.valueOf(courseDto.getPrice())) > 0) {
            throw new IllegalArgumentException("Iznos uplate (" + request.getAmount() + ") je veći od cene kursa. Cena kursa je " + courseDto.getPrice() + ". Molimo vas da uplatite tačan iznos.");
        }
    
       
        Transaction transaction = new Transaction();
        transaction.setId(generateTransactionId());
        transaction.setUserId(request.getUserId());
        transaction.setCourseId(request.getCourseId());
        transaction.setAmount(request.getAmount());
        transaction.setStatus("PENDING");
        transaction.setTimestamp(LocalDateTime.now());
    
       
        boolean isApproved = simulateBankApproval(request);
        transaction.setStatus(isApproved ? "APPROVED" : "DECLINED");
    
      
        paymentRepository.save(transaction);
    
      
        if ("APPROVED".equals(transaction.getStatus())) {
            try {
                String message = String.format("Payment approved for user %s on course %s", transaction.getUserId(), transaction.getCourseId());
                rabbitTemplate.convertAndSend("notification_payment_queue_exchange", "notification_payment_key", message);
                System.out.println("Notification sent to RabbitMQ: " + message);
            } catch (Exception e) {
                System.err.println("Failed to send notification to RabbitMQ: " + e.getMessage());
            }
        }
    
        UserDTO userDto = remoteServiceClient.getUserById(transaction.getUserId());
        return new TransactionWithUserAndCourseDTO(transaction, userDto, courseDto);
    }

    private boolean simulateBankApproval(PaymentRequest request) {

        return Math.random() > 0.1;
    }

    private Long generateTransactionId() {

        return System.currentTimeMillis();
    }

    public List<TransactionWithUserAndCourseDTO> getAll() {

        List<Transaction> transactions = paymentRepository.findAll();
        return transactions.stream().map(trans -> {

            UserDTO userDto = remoteServiceClient.getUserById(trans.getUserId());
            CourseDTO courseDto = remoteServiceClient.getCourseById(trans.getCourseId());
            //UserDTO userDto = userServiceProxy.getUserById(trans.getUserId());
            //CourseDTO courseDto = courseServiceProxy.getCourseById(trans.getCourseId());
            return new TransactionWithUserAndCourseDTO(trans, userDto, courseDto);
        }).collect(Collectors.toList());
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public void updatePayment(Transaction trans) {
        paymentRepository.save(trans);
    }

    public Transaction paymentApprovement(Long userId, Long courseId) {

        List<Transaction> transactions = paymentRepository.getByUserId(userId);
        return transactions.stream()
                .filter(transaction -> transaction.getCourseId().equals(courseId))
                .findFirst()
                .orElse(null);

    }

    
    public Transaction getById(Long id) {
        Transaction transaction = paymentRepository.findById(id).get();
        
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction sa ID-jem " + id + " ne postoji!");
        }
        return transaction;
    }

    


}
