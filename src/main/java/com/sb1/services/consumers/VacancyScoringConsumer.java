package com.sb1.services.consumers;

import com.sb1.constants.KafkaTopicsConstants;
import com.sb1.services.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyScoringConsumer {

    private final VacancyService vacancyService;

    @KafkaListener(topics = "topic-scoring", groupId = "vacancy-group")
    public void listen(Long vacancyId) {
        log.info(KafkaTopicsConstants.RECEIVED_VACANCY_ID_FOR_SCORING, vacancyId);
        vacancyService.vacancyScoring(vacancyId, 1L); // TODO id резюме пока хардкод.
    }
}
