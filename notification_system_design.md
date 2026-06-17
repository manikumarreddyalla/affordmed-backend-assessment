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

Better query:

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