package com.mani.affordmed.service;

import com.mani.affordmed.dto.Notification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class NotificationPriorityService {

    public List<Notification> getTopNotifications(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            return new ArrayList<>();
        }

        // Use PriorityQueue to get top 10
        PriorityQueue<ScoredNotification> pq = new PriorityQueue<>((a, b) -> {
            // Reverse order: highest score first
            return Double.compare(b.score, a.score);
        });

        for (Notification notif : notifications) {
            double score = calculateScore(notif);
            pq.add(new ScoredNotification(notif, score));
        }

        List<Notification> result = new ArrayList<>();
        int count = 0;
        while (!pq.isEmpty() && count < 10) {
            result.add(pq.poll().notification);
            count++;
        }

        return result;
    }

    private double calculateScore(Notification notification) {
        int typeWeight = getTypeWeight(notification.getType());
        double recencyScore = getRecencyScore(notification.getTimestamp());
        return typeWeight + recencyScore;
    }

    private int getTypeWeight(String type) {
        if (type == null) {
            return 1;
        }

        switch (type.toLowerCase()) {
            case "placement":
                return 5;
            case "result":
                return 4;
            case "event":
                return 3;
            default:
                return 1;
        }
    }

    private double getRecencyScore(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return 0;
        }

        try {
            long notifTime = parseTimestamp(timestamp);
            long now = System.currentTimeMillis();
            long ageMs = now - notifTime;
            long ageHours = ageMs / (1000 * 60 * 60);

            // Max 24 hours scored, newer = higher score
            if (ageHours > 24) {
                return 0;
            }
            return 24 - ageHours;
        } catch (Exception e) {
            return 0;
        }
    }

    private long parseTimestamp(String timestamp) {
        try {
            // Try ISO instant format
            return Instant.parse(timestamp).toEpochMilli();
        } catch (Exception e1) {
            try {
                // Try offset datetime format
                return OffsetDateTime.parse(timestamp).toInstant().toEpochMilli();
            } catch (Exception e2) {
                try {
                    // Try local datetime format
                    return LocalDateTime.parse(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();
                } catch (Exception e3) {
                    throw new RuntimeException("Cannot parse timestamp: " + timestamp);
                }
            }
        }
    }

    private static class ScoredNotification {
        Notification notification;
        double score;

        ScoredNotification(Notification notification, double score) {
            this.notification = notification;
            this.score = score;
        }
    }
}
