package com.mani.affordmed.controller;

import com.mani.affordmed.dto.SchedulerResult;
import com.mani.affordmed.dto.Vehicle;
import com.mani.affordmed.client.AffordMedClient;
import com.mani.affordmed.service.VehicleSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    @Autowired
    private AffordMedClient affordMedClient;

    @Autowired
    private VehicleSchedulerService schedulerService;

    @GetMapping
    public ResponseEntity<SchedulerResult> getSchedule(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Get vehicles from external API
        List<Vehicle> vehicles = affordMedClient.getVehicles(authHeader);

        // Default capacity (mechanic hours)
        int capacity = 40;

        // Solve knapsack problem
        SchedulerResult result = schedulerService.solveKnapsack(vehicles, capacity);

        return ResponseEntity.ok(result);
    }
}
