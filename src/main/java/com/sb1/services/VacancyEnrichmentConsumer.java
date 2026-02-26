package com.sb1.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyEnrichmentConsumer {

    private final VacancyService vacancyService;

    @KafkaListener(topics = "topic-1", groupId = "vacancy-group")
    public void listen(String vacancyId) {
        log.info("Received vacancy id {} for enrichment", vacancyId);
        vacancyService.enrichmentVacancy(Long.valueOf(vacancyId));
    }
}
