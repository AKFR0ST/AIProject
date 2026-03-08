package com.sb1.services;

import com.sb1.clients.HhClient;
import com.sb1.constants.KafkaTopicsConstants;
import com.sb1.constants.LlmPrompts;
import com.sb1.constants.VacancyServiceConstants;
import com.sb1.dto.HhVacancyDetailDto;
import com.sb1.dto.HhVacancyDto;
import com.sb1.dto.VacancyUserDecision;
import com.sb1.enums.LLMServices;
import com.sb1.enums.Senders;
import com.sb1.enums.UserDecision;
import com.sb1.interfaces.LLMInterfaceImpl;
import com.sb1.mappers.VacancyMapper;
import com.sb1.models.hh.Resume;
import com.sb1.models.hh.Vacancy;
import com.sb1.repositories.ResumeRepository;
import com.sb1.repositories.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static com.sb1.constants.LlmPrompts.COVER_LETTER_TEMPLATE;
import static com.sb1.constants.LlmPrompts.SCORE_TEMPLATE;
import static com.sb1.enums.Senders.HH;
import static com.sb1.enums.VacancyStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {

    private final HhClient hhClient;
    private final VacancyRepository vacancyRepository;
    private final ResumeRepository resumeRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final LLMInterfaceImpl sendRequestImpl;
    private final TelegramService telegramService;
    private final HhApplyService hhApplyService;

    @Autowired
    private VacancyMapper vacancyMapper;

    @Value("${hh.search.per-page}")
    private int perPage;

    private OffsetDateTime lastCheckTime = OffsetDateTime.now().minusHours(72);  // Стартовая бработка вакансий за последние 3 суток.

    @Value("${general.llm.default}")
    private String llmDefault;

    @Value("${general.default.sender}")
    private Senders defaultSender;

    @Value("${general.default.minimum.scoring}")
    private int minScore;

    @Scheduled(fixedDelayString = "${hh.search.delay}") // каждые 10 минут
    public void fetchAndStoreNewVacancy() {

        String dateFrom = lastCheckTime
                .truncatedTo(ChronoUnit.SECONDS)
                .withOffsetSameInstant(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        log.info("lastCheckTime: {}", dateFrom);

        List<HhVacancyDto> vacancies = hhClient.searchAllVacancies("Java разработчик", 96,  "between1And3", "publication_time", perPage, dateFrom);

        lastCheckTime = OffsetDateTime.now();

        if (Objects.isNull(vacancies)) {
            return;
        }

        for (HhVacancyDto dto : vacancies) {

            if (!vacancyRepository.existsByHhId(dto.getId())) {

                Vacancy vacancy = vacancyMapper.toEntity(dto);
                vacancyRepository.save(vacancy);
                log.info(VacancyServiceConstants.NEW_VACANCY_WITH_ID_SAVED, vacancy.getId(), vacancy.getName());

                kafkaTemplate.send("topic-detailed", vacancy.getId())
                        .whenComplete((result, ex) -> {
                            if (Objects.isNull(ex)) {
                                log.info(KafkaTopicsConstants.SUCCESS_TO_SEND_MESSAGE_ABOUT_VACANCY_WITH_ID_AND_STATUS_TO_TOPIC, vacancy.getId(), NEW, "topic-detailed");
                            } else {
                                log.error(KafkaTopicsConstants.FAILED_TO_SEND_MESSAGE_FOR_VACANCY_TO_TOPIC, vacancy.getId(), "topic-detailed", ex);
                            }
                        });
            }
        }
    }

    public void detailVacancy(Long id){
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(id))
                .orElseThrow();

        HhVacancyDetailDto detail = hhClient.getVacancyById(vacancy.getHhId());

        if (Objects.isNull(detail)) {

            kafkaTemplate.send("topic-detailed", vacancy.getId())
                    .whenComplete((result, ex) -> {
                        if (Objects.isNull(ex)) {
                            log.info(KafkaTopicsConstants.SUCCESS_TO_SEND_MESSAGE_ABOUT_VACANCY_WITH_ID_AND_STATUS_TO_TOPIC, vacancy.getId(), DETAILED, "topic-scoring");
                        } else {
                            log.error(KafkaTopicsConstants.FAILED_TO_SEND_MESSAGE_FOR_VACANCY_TO_TOPIC, vacancy.getId(), "topic-scoring", ex);
                        }
                    });
        }
        else {

            vacancyMapper.updateFromDetail(detail, vacancy);

            vacancy.setStatus(DETAILED);

            vacancyRepository.save(vacancy);

            log.info(VacancyServiceConstants.VACANCY_WITH_ID_DETAILED, vacancy.getId(), vacancy.getName());

            kafkaTemplate.send("topic-scoring", vacancy.getId())
                    .whenComplete((result, ex) -> {
                        if (Objects.isNull(ex)) {
                            log.info(KafkaTopicsConstants.SUCCESS_TO_SEND_MESSAGE_ABOUT_VACANCY_WITH_ID_AND_STATUS_TO_TOPIC, vacancy.getId(), DETAILED, "topic-scoring");
                        } else {
                            log.error(KafkaTopicsConstants.FAILED_TO_SEND_MESSAGE_FOR_VACANCY_TO_TOPIC, vacancy.getId(), "topic-scoring", ex);
                        }
                    });
        }
    }

    public void createCoverLetter(Long id) {
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(id))
                .orElseThrow();

        //  Получить резюме
        Resume resume = resumeRepository.findById(1L).orElseThrow(); // TODO Пока хардкод


        //  Передать в ЛЛМ и записать ответ в базу
        String prompt = String.format(COVER_LETTER_TEMPLATE, vacancy, resume);

        String coverLetter = sendRequestImpl.sendTextToTextRequest(
                LlmPrompts.PROFESSIONAL_RECRUTER,
                prompt,
                LLMServices.valueOf(llmDefault)
        );

        vacancy.setCoverLetter(coverLetter);

        vacancy.setStatus(READY_COVERAGE_LETTER);

        vacancyRepository.save(vacancy);
        log.info(VacancyServiceConstants.COVER_LETTER_FOR_VACANCY_WITH_ID_READY, vacancy.getId());

        try {
            telegramService.sendVacancy(519674552L, vacancy);
            vacancy.setStatus(SENT_TO_USER_FOR_APPROVE);
            vacancyRepository.save(vacancy);
        }
        catch (TelegramApiException e) {
            log.error(VacancyServiceConstants.FAILED_TO_SEND_MESSAGE_ABOUT_VACANCY_TO_USER_WITH_TELEGRAM, vacancy.getId(), e);
        }

    }

    public void vacancyScoring(Long vacancyId, Long resumeId){
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(vacancyId))
                .orElseThrow();
        Resume resume = resumeRepository.findById(resumeId).orElseThrow();

        String prompt = String.format(SCORE_TEMPLATE,
                vacancy.getDescription(),
                resume.toString()
        );

        String score = sendRequestImpl.sendTextToTextRequest(
                LlmPrompts.YOU_ARE_ATS_SYSTEM,
                prompt,
                LLMServices.valueOf(llmDefault)
        );

        if(Integer.parseInt(score)>=minScore) {
            vacancy.setStatus(SUCCESS_SCORING);
            kafkaTemplate.send("topic-coverLetter", vacancy.getId())
                    .whenComplete((result, ex) -> {
                        if (Objects.isNull(ex)) {
                            log.info(KafkaTopicsConstants.MESSAGE_WITH_STATUS_SENT_TO_KAFKA_FOR_VACANCY, vacancy.getStatus(), vacancy.getId());
                        } else {
                            log.error(KafkaTopicsConstants.FAILED_TO_SEND_MESSAGE_FOR_VACANCY_TO_TOPIC, vacancy.getId(), "topic-coverLetter", ex);
                        }
                    });
            log.info(VacancyServiceConstants.VACANCY_WITH_ID_SCORING_SUCCESS_RESULT, vacancyId, score);
        }
        else {
            vacancy.setStatus(BAD_SCORING);
            log.info(VacancyServiceConstants.VACANCY_WITH_ID_SCORING_BAD_RESULT, vacancyId, score);
        }
        vacancyRepository.save(vacancy);
    }

    public void processingUserDecision(VacancyUserDecision decision) {
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(decision.getVacancyId())).orElseThrow();
        if (decision.getDecision().equals(UserDecision.APPROVE)){
            vacancy.setStatus(READY_FOR_SENDING_TO_MAILER);
            vacancyRepository.save(vacancy);
            log.info(VacancyServiceConstants.VACANCY_WITH_ID_APPROVED_BY_USER, decision.getVacancyId());

            //  Отправка отклика через выбранный сендер
            if(HH.equals(defaultSender)){
                hhApplyService.apply(decision.getVacancyId());
            }

        }
        else{
            vacancy.setStatus(REJECTED_BY_USER);
            vacancyRepository.save(vacancy);
            log.info(VacancyServiceConstants.VACANCY_WITH_ID_REJECTED_BY_USER, decision.getVacancyId());
        }

    }
}
