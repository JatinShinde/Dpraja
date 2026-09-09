package com.spdpboss.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AutoUpdateService {

    @Autowired
    private ResultService resultService;

    private static final String API_URL = "https://result-hub-api.onrender.com/api/v1/live?apiKey=key_2c8841af9a924861";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                fetchApiResults();
            } catch (Exception e) {
                System.err.println("Init sync error: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Polls Result Hub API every 10 seconds to keep all market results, admin panel, and charts in sync.
     */
    @Scheduled(fixedRate = 10000)
    public void fetchApiResults() {
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Origin", "https://dpraja.com");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                    API_URL, org.springframework.http.HttpMethod.GET, entity, String.class);
            String jsonBody = response.getBody();

            if (jsonBody != null && !jsonBody.trim().isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(jsonBody);
                if (rootNode.isArray()) {
                    for (JsonNode item : rootNode) {
                        String marketName = item.path("marketName").asText("").trim();
                        String resultDisplay = item.path("resultDisplay").asText("").trim();
                        String openTime = item.path("openTime").asText("").trim();
                        String closeTime = item.path("closeTime").asText("").trim();

                        if (!marketName.isEmpty()) {
                            resultService.updateFromResultHub(marketName, resultDisplay, openTime, closeTime);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ RESULT HUB API SYNC ERROR: " + e.getMessage());
        }
    }
}


