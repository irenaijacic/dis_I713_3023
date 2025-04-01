package com.example.notification_service;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NotificationListener {

    @Autowired
    NotificationRepository notificationRepository;

    @RabbitListener(queues = "notification_payment_queue")
    public void handlePaymentNotification(String message) {
        System.out.println("Received message from RabbitMQ: " + message);

        // Pretpostavljamo da je poruka u formatu: "Payment approved for user 123 on
        // course 456"
        String[] parts = message.split(" ");
        String userId = parts[4];
        String courseId = parts[7];
        Long userIdLong = Long.parseLong(userId);

        String notificationMessage = " Payment for course " + courseId + " has been approved!";

        Notification newNot = new Notification(userIdLong, notificationMessage);
        notificationRepository.save(newNot);
    }

    @RabbitListener(queues = "notification_course_queue")
    public void handlePaymentNotification1(String message) {
        System.out.println("Received message from RabbitMQ 1111: " + message);

        // Pretpostavljamo da je poruka u formatu: "Payment approved for user 123 on
        // course 456"
        String[] parts = message.split(" ");
        String userId1 = parts[4];
        String courseId1 = parts[7];
        Long userIdLong1 = Long.parseLong(userId1);

        String notificationMessage1 = "Your enrollment for course " + courseId1 + " has been successful!";

        Notification newNot = new Notification(userIdLong1, notificationMessage1);
        notificationRepository.save(newNot);
    }

    public List<String> fallbackForUser(Long userId, Throwable throwable) {
        return List.of("Fallback: User Service is unavailable");
    }
}
