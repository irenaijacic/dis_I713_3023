package com.example.payment_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.example.payment_service.dtos.CourseDTO;
import com.example.payment_service.dtos.TransactionWithUserAndCourseDTO;
import com.example.payment_service.dtos.UserDTO;
import com.example.payment_service.models.Transaction;
import com.example.payment_service.models.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PaymentController.class)
class PaymentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Jwt createJwtToken(String... authorities) {
        return Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "user")
            .claim("scope", String.join(" ", authorities))
            .claim("scp", authorities)
            .build();
    }

    @Test
    void whenCreatePaymentWithValidAmount_thenSuccess() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setCourseId(1L);
        request.setAmount(new BigDecimal("100"));
        request.setPaymentMethod("CARD");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUserId(1L);
        transaction.setCourseId(1L);
        transaction.setAmount(new BigDecimal("100"));
        transaction.setStatus("APPROVED");
        transaction.setTimestamp(LocalDateTime.now());

        UserDTO userDTO = new UserDTO("testuser", "test@example.com");
        CourseDTO courseDTO = new CourseDTO("Test Course", "Test Description", 100.0f);
        TransactionWithUserAndCourseDTO responseDTO = new TransactionWithUserAndCourseDTO(transaction, userDTO, courseDTO);

        when(service.processPayment(any(PaymentRequest.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/payments/create")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(createJwtToken("user")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void whenCreatePaymentWithInvalidAmount_thenBadRequest() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setCourseId(1L);
        request.setAmount(new BigDecimal("50"));
        request.setPaymentMethod("CARD");

        when(service.processPayment(any(PaymentRequest.class)))
                .thenThrow(new IllegalArgumentException("Iznos uplate (50) nije dovoljan za plaćanje kursa. Cena kursa je 100"));

        mockMvc.perform(post("/payments/create")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(createJwtToken("user")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetAllPayments_thenSuccess() throws Exception {
        Transaction transaction = new Transaction();
        UserDTO userDTO = new UserDTO("testuser", "test@example.com");
        CourseDTO courseDTO = new CourseDTO("Test Course", "Test Description", 100.0f);
        TransactionWithUserAndCourseDTO dto = new TransactionWithUserAndCourseDTO(transaction, userDTO, courseDTO);

        when(service.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/payments")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(createJwtToken("user"))))
                .andExpect(status().isOk());
    }

    @Test
    void whenCheckTransactionApproved_thenSuccess() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100"));
        transaction.setStatus("APPROVED");

        when(service.paymentApprovement(1L, 1L)).thenReturn(transaction);

        mockMvc.perform(get("/payments/forCourse")
                .param("userId", "1")
                .param("courseId", "1")
                .param("price", "100")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(createJwtToken("SCOPE_internal"))))
                .andExpect(status().isOk());
    }

    @Test
    void whenDeletePayment_thenSuccess() throws Exception {
        Transaction transaction = new Transaction();
        when(service.getById(1L)).thenReturn(transaction);

        mockMvc.perform(delete("/payments/{id}", 1L)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(createJwtToken("admin"))))
                .andExpect(status().isOk());
    }
}