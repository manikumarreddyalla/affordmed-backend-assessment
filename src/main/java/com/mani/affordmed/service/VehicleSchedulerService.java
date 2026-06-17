package com.mani.affordmed.service;

import com.mani.affordmed.dto.Vehicle;
import com.mani.affordmed.dto.SchedulerResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleSchedulerService {

    public SchedulerResult solveKnapsack(List<Vehicle> vehicles, int capacity) {
        if (vehicles == null || vehicles.isEmpty() || capacity <= 0) {
            return new SchedulerResult(new ArrayList<>(), 0);
        }

        int n = vehicles.size();

        // Create DP table: dp[i][w] = max impact using first i vehicles with capacity w
        int[][] dp = new int[n + 1][capacity + 1];

        // Fill DP table
        for (int i = 1; i <= n; i++) {
            Vehicle vehicle = vehicles.get(i - 1);

            // Handle null safety
            if (vehicle.getDuration() == null || vehicle.getImpact() == null) {
                continue;
            }

            int duration = vehicle.getDuration();
            int impact = vehicle.getImpact();

            for (int w = 1; w <= capacity; w++) {
                // Option 1: Don't take this vehicle
                dp[i][w] = dp[i - 1][w];

                // Option 2: Take this vehicle if it fits
                if (duration <= w) {
                    int newImpact = dp[i - 1][w - duration] + impact;
                    dp[i][w] = Math.max(dp[i][w], newImpact);
                }
            }
        }

        // Backtrack to find which vehicles were selected
        List<Vehicle> selectedTasks = new ArrayList<>();
        int w = capacity;
        for (int i = n; i > 0 && w > 0; i--) {
            // If value came from including this vehicle
            if (dp[i][w] != dp[i - 1][w]) {
                Vehicle vehicle = vehicles.get(i - 1);
                selectedTasks.add(vehicle);
                w -= vehicle.getDuration();
            }
        }

        int totalImpact = dp[n][capacity];

        return new SchedulerResult(selectedTasks, totalImpact);
    }
}
