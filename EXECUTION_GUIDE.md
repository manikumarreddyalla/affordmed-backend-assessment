# How to Run

## Start the App
```bash
cd c:\Users\allas\Downloads\vehicle-maintenance-scheduler
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
java -jar target/vehicle-maintenance-scheduler-0.0.1-SNAPSHOT.jar
```

Server runs on `http://localhost:9091`

## Test Endpoints

### Vehicle Scheduling
```powershell
Invoke-RestMethod -Uri "http://localhost:9091/scheduler" -Method GET | ConvertTo-Json
```

Returns best vehicles to service within 40 hours budget.

### Top Notifications  
```powershell
Invoke-RestMethod -Uri "http://localhost:9091/notifications/top" -Method GET | ConvertTo-Json
```

Returns top 10 notifications by priority.

---

## With Auth Header (Real API)
```powershell
$headers = @{
    "Authorization" = "Bearer YOUR_TOKEN"
}
$response = Invoke-RestMethod -Uri "http://localhost:9091/scheduler" -Headers $headers -Method GET
$response | ConvertTo-Json
```

---

## Implementation

**Vehicle Scheduler**: Uses 0/1 Knapsack with DP. O(n × capacity).

**Priority Notifications**: Uses weighted scoring. Placement(5) > Result(4) > Event(3). Recent = higher.

Both services fall back to demo data if API not available.
