package com.example.notification_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTests {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationListener notificationListener;


    @Test
    void whenHandlePaymentNotification_thenSaveNotification() {
        String message = "Payment approved for user 123 on course 456";
        
        notificationListener.handlePaymentNotification(message);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void whenHandleCourseNotification_thenSaveNotification() {
        String message = "Payment approved for user 123 on course 456";
        
        notificationListener.handlePaymentNotification1(message);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}