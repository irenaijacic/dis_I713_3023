package com.example.notification_service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class NotificationRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void whenFindByUserId_thenReturnNotifications() {
        // Kreiranje test notifikacije
        Notification notification = new Notification(1L, "Test message");
        entityManager.persist(notification);
        entityManager.flush();

        // Kada
        List<Notification> found = notificationRepository.findByUserId(1L);

        // Tada
        assertFalse(found.isEmpty());
        assertEquals(notification.getMessage(), found.get(0).getMessage());
    }

    @Test
    void whenSaveNotification_thenNotificationIsSaved() {
        // Kreiranje test notifikacije
        Notification notification = new Notification(1L, "Test message");
        
        // Kada
        Notification saved = notificationRepository.save(notification);

        // Tada
        assertNotNull(saved.getId());
        assertEquals(notification.getMessage(), saved.getMessage());
    }
}