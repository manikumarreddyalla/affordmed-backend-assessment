package com.mani.affordmed.client;

import com.mani.affordmed.dto.Depot;
import com.mani.affordmed.dto.Notification;
import com.mani.affordmed.dto.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class AffordMedClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${affordmed.base-url:http://localhost:8080}")
    private String baseUrl;

    public List<Depot> getDepots(String authHeader) {
        try {
            String url = baseUrl + "/depots";
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null && !authHeader.isEmpty()) {
                headers.set("Authorization", authHeader);
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Depot>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Depot>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
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

            ResponseEntity<List<Vehicle>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Vehicle>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
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

            ResponseEntity<List<Notification>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Notification>>() {}
            );

            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
