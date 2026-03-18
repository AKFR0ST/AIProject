package com.sb1.services.consumers;

import com.sb1.constants.KafkaTopicsConstants;
import com.sb1.exceptions.RetryableException;
import com.sb1.services.VacancyService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyDetailedConsumer {

    private final VacancyService vacancyService;

    @RetryableTopic(
            attempts = "6",
            include = { RetryableException.class,
                    CallNotPermittedException.class},
            backoff = @Backoff(
                    delay = 10000,
                    multiplier = 3.0
            ),
            dltTopicSuffix = "-dlt",
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = "topic-detailed", groupId = "vacancy-group")
    public void listen(Long vacancyId) {
        log.info(KafkaTopicsConstants.RECEIVED_VACANCY_ID_FOR_DETAILED, vacancyId);
        vacancyService.detailVacancy(vacancyId);
    }

    @DltHandler
    public void handleDlt(Long vacancyId) {
        log.error("Message sent to DLT. vacancyId={}", vacancyId);
    }
}
