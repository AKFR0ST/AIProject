package com.sb1.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyScoringConsumer {

    public static final String RECEIVED_VACANCY_ID_FOR_SCORING = "Received vacancy id {} for scoring";
    private final VacancyService vacancyService;

    @KafkaListener(topics = "topic-scoring", groupId = "vacancy-group")
    public void listen(Long vacancyId) {
        log.info(RECEIVED_VACANCY_ID_FOR_SCORING, vacancyId);
        vacancyService.resumeForVacancyScoring(vacancyId, 1L); // TODO id резюме пока хардкод.
    }
}
