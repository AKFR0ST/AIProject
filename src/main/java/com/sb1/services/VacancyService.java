package com.sb1.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.sb1.clients.HhClient;
import com.sb1.models.hh.Vacancy;
import com.sb1.repositories.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final HhClient hhClient;
    private final VacancyRepository repository;

    @Value("${hh.search.text}")
    private String text;

    @Value("${hh.search.area}")
    private String area;

    @Value("${hh.search.per-page}")
    private int perPage;

    @Scheduled(fixedDelay = 600000) // каждые 10 минут
    public void fetchAndStore() {

        JsonNode root = hhClient.search(text, area, perPage);
        JsonNode items = root.get("items");

        for (JsonNode node : items) {

            String id = node.get("id").asText();

            if (!repository.existsById(id)) {

                Vacancy vacancy = new Vacancy();
                vacancy.setHhId(id);
                vacancy.setName(node.get("name").asText());
                vacancy.setEmployer(node.get("employer").get("name").asText());
                vacancy.setUrl(node.get("alternate_url").asText());
                vacancy.setPublishedAt(node.get("published_at").asText());

                repository.save(vacancy);
                System.out.println("NEW: " + vacancy.getName());
            }
        }
    }
}
