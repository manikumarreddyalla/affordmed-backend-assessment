package com.mani.affordmed.client;

import com.mani.affordmed.dto.Depot;
import com.mani.affordmed.dto.Notification;
import com.mani.affordmed.dto.Vehicle;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AffordMedClient {

    private static final Logger log = LoggerFactory.getLogger(AffordMedClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${affordmed.base-url:http://4.224.186.213/evaluation-service}")
    private String baseUrl;

    public List<Depot> getDepots(String authHeader) {
        try {
            String url = baseUrl + "/depots";
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null && !authHeader.isEmpty()) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);
            HttpMethod method = Objects.requireNonNull(HttpMethod.GET);

            ResponseEntity<DepotResponse> response = restTemplate.exchange(
                url,
                method,
                entity,
                new ParameterizedTypeReference<DepotResponse>() {}
            );

            if (response.getBody() == null || response.getBody().getDepots() == null) {
                return getDemoDepots();
            }
            return response.getBody().getDepots();
        } catch (Exception e) {
            log.warn("Using demo depots because upstream call failed: {}", e.getMessage());
            return getDemoDepots();
        }
    }

    public List<Vehicle> getVehicles(String authHeader) {
        try {
            String url = baseUrl + "/vehicles";
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null && !authHeader.isEmpty()) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);
            HttpMethod method = Objects.requireNonNull(HttpMethod.GET);

            ResponseEntity<VehicleResponse> response = restTemplate.exchange(
                url,
                method,
                entity,
                new ParameterizedTypeReference<VehicleResponse>() {}
            );

            if (response.getBody() == null || response.getBody().getVehicles() == null) {
                return getDemoVehicles();
            }
            return response.getBody().getVehicles();
        } catch (Exception e) {
            log.warn("Using demo vehicles because upstream call failed: {}", e.getMessage());
            return getDemoVehicles();
        }
    }

    public List<Notification> getNotifications(String authHeader) {
        try {
            String url = baseUrl + "/notifications";
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null && !authHeader.isEmpty()) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);
            HttpMethod method = Objects.requireNonNull(HttpMethod.GET);

            ResponseEntity<NotificationResponse> response = restTemplate.exchange(
                url,
                method,
                entity,
                new ParameterizedTypeReference<NotificationResponse>() {}
            );

            if (response.getBody() == null || response.getBody().getNotifications() == null) {
                return getDemoNotifications();
            }
            return response.getBody().getNotifications();
        } catch (Exception e) {
            log.warn("Using demo notifications because upstream call failed: {}", e.getMessage());
            return getDemoNotifications();
        }
    }

    private List<Depot> getDemoDepots() {
        List<Depot> depots = new ArrayList<>();

        Depot depot1 = new Depot();
        depot1.setId(1);
        depot1.setMechanicHours(20);
        depots.add(depot1);

        Depot depot2 = new Depot();
        depot2.setId(2);
        depot2.setMechanicHours(16);
        depots.add(depot2);

        return depots;
    }

    private List<Vehicle> getDemoVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();

        vehicles.add(new Vehicle("T101", 6, 18));
        vehicles.add(new Vehicle("T102", 4, 14));
        vehicles.add(new Vehicle("T103", 10, 30));
        vehicles.add(new Vehicle("T104", 8, 22));
        vehicles.add(new Vehicle("T105", 3, 9));

        return vehicles;
    }

    private List<Notification> getDemoNotifications() {
        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setId("N1");
        notification1.setType("placement");
        notification1.setMessage("Vehicle task T101 is ready for assignment.");
        notification1.setTimestamp(java.time.Instant.now().toString());
        notifications.add(notification1);

        Notification notification2 = new Notification();
        notification2.setId("N2");
        notification2.setType("result");
        notification2.setMessage("Task T103 has been approved.");
        notification2.setTimestamp(java.time.Instant.now().minusSeconds(3600).toString());
        notifications.add(notification2);

        Notification notification3 = new Notification();
        notification3.setId("N3");
        notification3.setType("event");
        notification3.setMessage("A new depot event is available.");
        notification3.setTimestamp(java.time.Instant.now().minusSeconds(7200).toString());
        notifications.add(notification3);

        return notifications;
    }

    private static class DepotResponse {
        @JsonProperty("depots")
        private List<Depot> depots;

        public List<Depot> getDepots() {
            return depots;
        }

        public void setDepots(List<Depot> depots) {
            this.depots = depots;
        }
    }

    private static class VehicleResponse {
        @JsonProperty("vehicles")
        private List<Vehicle> vehicles;

        public List<Vehicle> getVehicles() {
            return vehicles;
        }

        public void setVehicles(List<Vehicle> vehicles) {
            this.vehicles = vehicles;
        }
    }

    private static class NotificationResponse {
        @JsonProperty("notifications")
        private List<Notification> notifications;

        public List<Notification> getNotifications() {
            return notifications;
        }

        public void setNotifications(List<Notification> notifications) {
            this.notifications = notifications;
        }
    }
}
