package com.sb1.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyDetailedConsumer {

    public static final String RECEIVED_VACANCY_ID_FOR_DETAILED = "Received vacancy id {} for detailed";
    private final VacancyService vacancyService;

    @KafkaListener(topics = "topic-detailed", groupId = "vacancy-group")
    public void listen(Long vacancyId) {
        log.info(RECEIVED_VACANCY_ID_FOR_DETAILED, vacancyId);
        vacancyService.detailVacancy(vacancyId);
    }
}
