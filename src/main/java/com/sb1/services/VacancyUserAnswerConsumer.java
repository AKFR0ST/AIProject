package com.sb1.services;

import com.sb1.constants.KafkaTopicsConstants;
import com.sb1.dto.VacancyUserDecision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyUserAnswerConsumer {

    private final VacancyService vacancyService;

    @KafkaListener(topics = "topic-userAnswer", groupId = "vacancy-group")
    public void listen(VacancyUserDecision decision) {
        log.info(KafkaTopicsConstants.RECEIVED_VACANCY_ID_USER_ANSWER, decision.getVacancyId());

        vacancyService.processingUserDecision(decision);
    }
}