package com.sb1.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyCoverLetterConsumer {

    private final VacancyService vacancyService;

    @KafkaListener(topics = "topic-coverLetter", groupId = "vacancy-group")
    public void listen(Long vacancyId) {
        log.info("Received vacancy id {} for creating cover letter", vacancyId);
        vacancyService.createCoverLetter(vacancyId);
    }
}