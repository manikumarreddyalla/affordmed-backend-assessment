package com.mani.affordmed.controller;

import com.mani.affordmed.dto.Notification;
import com.mani.affordmed.client.AffordMedClient;
import com.mani.affordmed.service.NotificationPriorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private AffordMedClient affordMedClient;

    @Autowired
    private NotificationPriorityService notificationPriorityService;

    @GetMapping("/top")
    public ResponseEntity<List<Notification>> getTopNotifications(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Get all notifications from external API
        List<Notification> allNotifications = affordMedClient.getNotifications(authHeader);

        // Get top 10 by priority
        List<Notification> topNotifications = notificationPriorityService.getTopNotifications(allNotifications);

        return ResponseEntity.ok(topNotifications);
    }
}
