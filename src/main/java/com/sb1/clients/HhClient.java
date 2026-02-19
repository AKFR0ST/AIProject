package com.sb1.clients;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class HhClient {

    private final WebClient webClient;

    public HhClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.hh.ru")
                .defaultHeader("User-Agent", "hh-microservice")
                .build();
    }

    public JsonNode search(String text, String area, int perPage) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/vacancies")
                        .queryParam("text", text)
                        .queryParam("area", area)
                        .queryParam("per_page", perPage)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
