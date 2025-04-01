package com.example.payment_service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.CustomException;
import com.example.payment_service.dtos.TransactionWithUserAndCourseDTO;
import com.example.payment_service.models.Transaction;
import com.example.payment_service.models.PaymentRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        try{List<TransactionWithUserAndCourseDTO> transaction = service.getAll();
        return ResponseEntity.ok(transaction);}
         catch (CustomException e) {

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage(), "status", 503));
        }
    }

    @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request) {
        try {
            TransactionWithUserAndCourseDTO response = service.processPayment(request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (CustomException e) {

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", e.getMessage(), "status", 503));
        }catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
         } 

    }

    @PreAuthorize("hasAuthority('user') || hasAuthority('admin') || hasAuthority('SCOPE_internal')")
    @GetMapping("/forCourse")
    public Boolean isTransactionApproved(@RequestParam Long userId, @RequestParam Long courseId,
            @RequestParam(defaultValue = "0") float price) {

        Transaction transaction = service.paymentApprovement(userId, courseId);

        if (transaction == null) {
            return false;
        }
        BigDecimal fromFloat = BigDecimal.valueOf(price);
        String status = transaction.getStatus();
        if (transaction.getAmount().compareTo(fromFloat) != 0)
            return false;
        return "APPROVED".equals(status);

    }

    @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable("id") Long id) {
        try {
            Transaction trans = service.getById(id);
            return ResponseEntity.ok(trans);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable("id") Long id) {
        if (service.getById(id) == null) {
            ResponseEntity.status(404).body("Ova transakcija ne postoji!");
        }
        service.deletePayment(id);
        return ResponseEntity.status(200).body("Transakcija je obrisana!");
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<?> putPayment(@PathVariable("id") Long id, @RequestBody Transaction updatedTransaction) {
        Transaction transToUpdate = service.getById(id);

        if (transToUpdate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transakcija nije pronadjena!");
        }

        transToUpdate
                .setAmount(updatedTransaction.getAmount() != null ? updatedTransaction.getAmount()
                        : transToUpdate.getAmount());
        transToUpdate.setCourseId(
                updatedTransaction.getCourseId() != null ? updatedTransaction.getCourseId()
                        : transToUpdate.getCourseId());

        transToUpdate.setUserId(
                updatedTransaction.getUserId() != null ? updatedTransaction.getUserId() : transToUpdate.getUserId());

        service.updatePayment(transToUpdate);

        return ResponseEntity.ok(transToUpdate);
    }

}
