package com.example.notification_service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

 @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @GetMapping("/{userId}")
    public List<Notification> getNotificationsForUser(@PathVariable Long userId) {
        return notificationRepository.findByUserId(userId);
    }
 @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @GetMapping("")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationRepository.findAll());
    }

}