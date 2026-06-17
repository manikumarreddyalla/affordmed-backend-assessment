## Stage 3 - Query Optimization

Given query:

```sql
SELECT *
FROM notifications
WHERE studentID = 1042
AND isRead = false
ORDER BY createdAt DESC;
```

Why it is slow:

- `SELECT *` reads all columns even if not all are needed.
- There is no `LIMIT`.
- It does not use a useful composite index.

Better query in my view is:

```sql
SELECT id, type, message, createdAt
FROM notifications
WHERE studentID = 1042
AND isRead = false
ORDER BY createdAt DESC
LIMIT 50;
```

Why not index every column:

- Too many indexes slow down insert and update operations.
- Extra indexes take more storage.
- Not every column is used in filtering or sorting.

### Result query for placement notifications in last 7 days

```sql
SELECT DISTINCT student_id
FROM notifications
WHERE type = 'Placement'
AND created_at >= NOW() - INTERVAL '7 days';
```

## Stage 4 - Scaling

### Redis Cache

When we get many requests for the same notifications, the database gets hit too many times. We can store recent results in Redis (in-memory cache) so we don't need to query the database every time.

```java
// Check cache first
List<Notification> cached = redisCache.get("notifications_" + studentId);
if (cached != null) {
    return cached;  // Return from cache
}

// If not in cache, query database
List<Notification> notifications = database.query(studentId);

// Store in cache for 5 minutes
redisCache.set("notifications_" + studentId, notifications, 300);
return notifications;
```

### Pagination

Instead of loading all notifications at once, we load them in chunks (like 10 per page).

```java
// Get page 1 with 10 items
SELECT * FROM notifications 
WHERE student_id = 1042
LIMIT 10 OFFSET 0;

// Get page 2 with 10 items
LIMIT 10 OFFSET 10;
```

This makes the app faster because we send less data at a time.

### Background workers

Some tasks like sending emails or generating reports should not happen during a user request. Instead we put them in a queue and process them later with background workers.

```java
// When user requests report, just add to queue (fast)
queue.add(new GenerateReportTask(studentId));

// Background worker picks up task when it's free
while (true) {
    Task task = queue.take();
    processTask(task);  // Can take long time
}
```

### Cache invalidation

When a new notification arrives, the cache becomes old. We need to remove it so next request gets fresh data.

```java
// When new notification created
database.insert(notification);

// Invalidate cache
redisCache.delete("notifications_" + studentId);
```

### Trade-offs

- Cache helps speed but uses more memory
- Pagination reduces load but needs offset calculation
- Background workers make app responsive but adds complexity
- Invalidating cache too often defeats the purpose of caching

## Stage 5 - Queue-based architecture

### Kafka/RabbitMQ

Instead of direct function calls between services, we use a message queue. One service sends a message, another service receives and processes it later.

```
Service A: "Hey, new notification created!"
  |
  +---> Message Queue (Kafka/RabbitMQ)
           |
           +---> Service B: Process notification
           +---> Service C: Send email
           +---> Service D: Update cache
```

### Producer-Consumer

Service sending the message = Producer. Service receiving = Consumer.

```java
// Producer (NotificationService)
kafkaProducer.send("notification-topic", notification);

// Consumer (EmailService)
@KafkaListener(topic = "notification-topic")
public void handleNotification(Notification notification) {
    sendEmail(notification.getEmail());
}
```

### Retry mechanism

If something fails, try again automatically.

```java
@Retry(maxAttempts = 3, delay = 1000)  // Try 3 times, wait 1 sec between
public void processNotification(Notification notification) {
    sendEmail(notification.getEmail());
}
```

### Dead Letter Queue

If a message keeps failing after all retries, send it to a special "dead letter" queue for manual review.

```
Message fails 3 times
  |
  +---> Dead Letter Queue (for debugging)
           |
           +---> Human reviews why it failed
           +---> Fix and retry
```

### Idempotency

Process the same message multiple times and get same result (no duplicates).

```java
// If we send same message twice
kafkaProducer.send("topic", messageWithId);
kafkaProducer.send("topic", messageWithId);  // Same ID

// Consumer should check: did we already process this ID?
if (alreadyProcessed(messageId)) {
    return;  // Skip, don't do it again
}

process(message);
markAsProcessed(messageId);
```

Without idempotency, same notification might be sent twice, emails sent twice, etc.