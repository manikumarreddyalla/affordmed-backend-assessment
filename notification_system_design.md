# Campus Notification Platform - System Design

## Stage 1 - REST API Design

### Endpoints
- `GET /api/notifications` - get student's notifications  
- `GET /api/notifications/top` - get top 10
- `POST /api/notifications/:id/read` - mark as read
- `POST /api/notifications/notify-all` - send bulk notifications

### Response Format
```json
{
  "notifications": [
    {
      "id": "uuid",
      "type": "Placement",
      "message": "string",
      "timestamp": "2026-04-22T17:51:18Z",
      "isRead": false
    }
  ]
}
```

Realtime: Use WebSocket for live updates or long polling

---

## Stage 2 - Database Design

Use PostgreSQL. Has good JSON support and indexing.

```sql
CREATE TABLE notifications (
  id UUID PRIMARY KEY,
  student_id UUID,
  type VARCHAR(50),
  message TEXT,
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP
);

CREATE INDEX idx_student_unread 
ON notifications(student_id, is_read) 
WHERE is_read = FALSE;
```

Schema is simple. Just students and notifications table. Can add tags/categories later.

---

## Stage 3 - Query Optimization

### Slow Query
```sql
SELECT * FROM notifications
WHERE studentID = 1042 AND isRead = false
ORDER BY createdAt DESC;
```

Problems:
- `SELECT *` gets unnecessary columns
- No LIMIT, could return millions
- Missing index on (student_id, isRead, created_at)

### Better Query
```sql
SELECT id, type, message, created_at
FROM notifications
WHERE student_id = 1042 AND is_read = false
ORDER BY created_at DESC
LIMIT 50;
```

This is ~100x faster with proper indexing.

Don't index every column - slows down writes and wastes storage.

---

## Stage 4 - Scaling

When database gets slow with millions of notifications:

**Option 1: Caching**
- Store hot data in Redis
- 5-10 min expiry
- Faster reads but needs invalidation

**Option 2: Pagination**  
- Return 20-50 per page
- Load more on demand
- Reduces memory and network

**Option 3: Background Workers**
- Queue bulk operations (emails, notifications)
- Don't block user requests
- Use RabbitMQ or Kafka

---

## Stage 5 - Bulk Notifications (50K students)

### Problem with Simple Loop
```
for each student
  send email
  save to db  
  push notification
```

If email fails at student 200, what about rest? Database shows sent but email failed.

### Better Approach
1. Save to DB first (source of truth)
2. Send email async with retry
3. Failed items go to dead letter queue

```java
notificationRepository.save(notification);  // Always succeeds
emailService.sendWithRetry(email, message); // Can fail and retry
```

Use message queue (Kafka/RabbitMQ). Process asynchronously. Much faster.

---

## Stage 6 - Priority Inbox

Top 10 notifications by priority.

### Scoring
```
Score = Type Weight + Recency

Weights:
  Placement: 5
  Result: 4  
  Event: 3

Recency: 24 - hours_old (max 24)
```

### Example
- Placement 2h old: 5 + (24-2) = 27
- Event 1h old: 3 + (24-1) = 26

Use PriorityQueue for top 10. O(log n) per insert.

```java
PriorityQueue<Notification> pq = new PriorityQueue<>(
  (a, b) -> Double.compare(calculateScore(b), calculateScore(a))
);
```

Refresh top 10 every minute from all notifications.

---

## Summary

| Stage | What | How |
|-------|------|-----|
| 1 | REST API | 4 endpoints for CRUD |
| 2 | Database | PostgreSQL with basic schema |
| 3 | Speed | Indexes + pagination + LIMIT |
| 4 | Scaling | Cache + pagination + workers |
| 5 | Reliability | Queue + retry + DLQ |
| 6 | Ranking | Weighted score + priority queue |
