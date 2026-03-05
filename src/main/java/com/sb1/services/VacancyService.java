package com.sb1.services;

import com.sb1.clients.HhClient;
import com.sb1.dto.HhResponseDto;
import com.sb1.dto.HhVacancyDetailDto;
import com.sb1.dto.HhVacancyDto;
import com.sb1.dto.VacancyUserDecision;
import com.sb1.enums.LLMServices;
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

import java.util.Objects;

import static com.sb1.enums.VacancyStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {

    public static final String MESSAGE_WITH_STATUS_SENT_TO_KAFKA_FOR_VACANCY = "Message with status {} sent to Kafka for vacancy {}";
    public static final String VACANCY_WITH_ID_SCORING_SUCCESS_RESULT = "Vacancy with id {} scoring success, result: {}";
    public static final String VACANCY_WITH_ID_SCORING_BAD_RESULT = "Vacancy with id {} scoring fail, result: {}";
    public static final String SUCCESS_TO_SEND_MESSAGE_ABOUT_VACANCY_WITH_ID_AND_STATUS_TO_TOPIC = "Success to send message about vacancy with id {}, and status{} to topic{}";
    public static final String FAILED_TO_SEND_MESSAGE_FOR_VACANCY_TO_TOPIC = "Failed to send message for vacancy {}, to topic{}";
    public static final String NEW_VACANCY_WITH_ID_SAVED = "NEW vacancy with id {} saved: {}";
    public static final String VACANCY_WITH_ID_DETAILED = "Vacancy with id {} detailed: {}";
    public static final String COVER_LETTER_FOR_VACANCY_WITH_ID_READY = "Cover letter for vacancy with id {} ready";
    public static final String VACANCY_WITH_ID_APPROVED_BY_USER = "Vacancy with id {} approved by user";
    public static final String VACANCY_WITH_ID_REJECTED_BY_USER = "Vacancy with id {} rejected by user";
    private final HhClient hhClient;
    private final VacancyRepository vacancyRepository;
    private final ResumeRepository resumeRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final LLMInterfaceImpl sendRequestImpl;
    private final TelegramService telegramService;

    @Autowired
    private VacancyMapper vacancyMapper;

    @Value("${hh.search.area}")
    private String area;

    @Value("${hh.search.per-page}")
    private int perPage;

    @Value("${llm.default}")
    private String llmDefault;

    @Scheduled(fixedDelayString = "${hh.search.delay}") // каждые 10 минут
    public void fetchAndStoreNewVacancy() {

        HhResponseDto responseDto = hhClient.searchNewVacancies("Java разработчик", area, 96,  "between1And3", "publication_time", perPage );

        if (Objects.isNull(responseDto) || Objects.isNull(responseDto.getItems())) {
            return;
        }

        for (HhVacancyDto dto : responseDto.getItems()) {

            if (!vacancyRepository.existsByHhId(dto.getId())) {

                Vacancy vacancy = vacancyMapper.toEntity(dto);
                vacancyRepository.save(vacancy);
                log.info(NEW_VACANCY_WITH_ID_SAVED, vacancy.getId(), vacancy.getName());

                kafkaTemplate.send("topic-detailed", vacancy.getId())
                        .whenComplete((result, ex) -> {
                            if (Objects.isNull(ex)) {
                                log.info(SUCCESS_TO_SEND_MESSAGE_ABOUT_VACANCY_WITH_ID_AND_STATUS_TO_TOPIC, vacancy.getId(), NEW, "topic-detailed");
                            } else {
                                log.error(FAILED_TO_SEND_MESSAGE_FOR_VACANCY_TO_TOPIC, vacancy.getId(), "topic-detailed", ex);
                            }
                        });
            }
        }
    }

    public void detailVacancy(Long id){
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(id))
                .orElseThrow();

        HhVacancyDetailDto detail = hhClient.getVacancyById(vacancy.getHhId());

        vacancyMapper.updateFromDetail(detail, vacancy);

        vacancy.setStatus(DETAILED);

        vacancyRepository.save(vacancy);

        log.info(VACANCY_WITH_ID_DETAILED, vacancy.getId(), vacancy.getName());

        kafkaTemplate.send("topic-scoring", vacancy.getId())
                .whenComplete((result, ex) -> {
                    if (Objects.isNull(ex)) {
                        log.info(SUCCESS_TO_SEND_MESSAGE_ABOUT_VACANCY_WITH_ID_AND_STATUS_TO_TOPIC, vacancy.getId(), DETAILED, "topic-scoring");
                    } else {
                        log.error(FAILED_TO_SEND_MESSAGE_FOR_VACANCY_TO_TOPIC, vacancy.getId(), "topic-scoring", ex);
                    }
                });
    }

    public void createCoverLetter(Long id) {
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(id))
                .orElseThrow();

        //  Получить резюме
        Resume resume = resumeRepository.findById(1L).orElseThrow(); // TODO Пока хардкод


        //  Передать в ЛЛМ и записать ответ в базу
        String coverLetter = sendRequestImpl.sendTextToTextRequest("Профессиональный рекрутер", "Ты — IT-рекрутер и бывший технический специалист. Пишешь лаконичные, содержательные сопроводительные письма без воды.\n" +
                "\n" +
                "Вход:\n" +
                "\n" +
                "Вакансия: \n" + vacancy +
                "\n" +
                "Резюме: \n" + resume +
                "\n" +
                "Алгоритм:\n" +
                "\n" +
                "Определи ключевой стек и основные задачи роли.\n" +
                "\n" +
                "Выдели релевантный коммерческий опыт кандидата.\n" +
                "\n" +
                "Подчеркни 1–2 достижения (если есть метрики — используй их).\n" +
                "\n" +
                "Сформулируй ценность кандидата для команды.\n" +
                "\n" +
                "Формат письма:\n" +
                "\n" +
                "5–6 предложений\n" +
                "\n" +
                "конкретика по стеку\n" +
                "\n" +
                "без клише\n" +
                "\n" +
                "без повторения всего резюме\n" +
                "\n" +
                "без выдуманных фактов\n" +
                "\n" +
                "Стиль: уверенный, технически грамотный, без избыточной формальности.\n" +
                "Вывод: только итоговое письмо.",
                LLMServices.valueOf(llmDefault));

        vacancy.setCoverLetter(coverLetter);

        vacancy.setStatus(READY_COVERAGE_LETTER);

        vacancyRepository.save(vacancy);
        log.info(COVER_LETTER_FOR_VACANCY_WITH_ID_READY, vacancy.getId());

        try {
            telegramService.sendVacancy(519674552L, vacancy);
            vacancy.setStatus(SENT_TO_USER_FOR_APPROVE);
            vacancyRepository.save(vacancy);
        }
        catch (TelegramApiException e) {
            log.error("Failed to send message about vacancy {} to user with telegram", vacancy.getId(), e);
        }



//        kafkaTemplate.send("topic-4", vacancy.getId())
//                .whenComplete((result, ex) -> {
//                    if (Objects.isNull(ex)) {
//                        log.info(MESSAGE_WITH_STATUS_SENT_TO_KAFKA_FOR_VACANCY, vacancy.getStatus(), vacancy.getId());
//                    } else {
//                        log.error("Failed to send message for vacancy {}", vacancy.getId(), ex);
//                    }
//                });

    }

    public void resumeForVacancyScoring(Long vacancyId, Long resumeId){
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(vacancyId))
                .orElseThrow();
        Resume resume = resumeRepository.findById(resumeId).orElseThrow();

        String score = sendRequestImpl.sendTextToTextRequest("Ты — система предварительного отбора кандидатов (ATS) с функцией экспертной оценки.", "Входные данные:\n" +
                "\n" +
                "Вакансия: " + vacancy.getDescription() +
                "\n" +
                "Резюме: \n" + resume.toString() +
                "\n" +
                "Правила оценки:\n" +
                "\n" +
                "60% веса — обязательные технические требования\n" +
                "\n" +
                "20% веса — релевантный опыт и уровень\n" +
                "\n" +
                "10% веса — дополнительные навыки\n" +
                "\n" +
                "10% веса — соответствие задачам роли\n" +
                "\n" +
                "Если отсутствуют ключевые обязательные требования — оценка не может быть выше 60.\n" +
                "\n" +
                "Не придумывай недостающий опыт.\n" +
                "Не завышай оценку без объективных оснований.\n" +
                "\n" +
                "Верни:\n" +
                "\n" +
                "Итоговую оценку (0–100)\n" +
                "\n" +
                "Краткое пояснение, какие требования совпадают\n" +
                "\n" +
                "Ключевые пробелы\n" +
                "\n" +
                "В ответе должна быть только итоговая оценка",
                LLMServices.valueOf(llmDefault));

        if(Integer.parseInt(score)>=10) {
            vacancy.setStatus(SUCCESS_SCORING);
            kafkaTemplate.send("topic-coverLetter", vacancy.getId())
                    .whenComplete((result, ex) -> {
                        if (Objects.isNull(ex)) {
                            log.info(MESSAGE_WITH_STATUS_SENT_TO_KAFKA_FOR_VACANCY, vacancy.getStatus(), vacancy.getId());
                        } else {
                            log.error(FAILED_TO_SEND_MESSAGE_FOR_VACANCY_TO_TOPIC, vacancy.getId(), "topic-coverLetter", ex);
                        }
                    });
            log.info(VACANCY_WITH_ID_SCORING_SUCCESS_RESULT, vacancyId, score);
        }
        else {
            vacancy.setStatus(BAD_SCORING);
            log.info(VACANCY_WITH_ID_SCORING_BAD_RESULT, vacancyId, score);
        }
        vacancyRepository.save(vacancy);
    }

//    public void getApproveByUser(Long vacancyId) throws TelegramApiException {
//        Vacancy vacancy = vacancyRepository.findById(String.valueOf(vacancyId)).orElseThrow();
//        telegramService.sendVacancy(519674552L, vacancy);
//    }

//    public void approveVacancy(Long vacancyId) {
//        Vacancy vacancy = vacancyRepository.findById(String.valueOf(vacancyId)).orElseThrow();
//        vacancy.setStatus(READY_FOR_SENDING_TO_MAILER);
//        vacancyRepository.save(vacancy);
//        log.info("Vacancy with id {} approved by user", vacancyId);
//        // TODO отправка на почту.
//
//    }
//
//    public void rejectVacancy(Long vacancyId) {
//        Vacancy vacancy = vacancyRepository.findById(String.valueOf(vacancyId)).orElseThrow();
//        vacancy.setStatus(REJECTED_BY_USER);
//        vacancyRepository.save(vacancy);
//        log.info("Vacancy with id {} rejected by user", vacancyId);
//    }

    public void processingUserDecision(Long vacancyId, VacancyUserDecision decision) {
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(vacancyId)).orElseThrow();
        if (decision.decision().equals(UserDecision.APPROVE)){
            vacancy.setStatus(READY_FOR_SENDING_TO_MAILER);
            vacancyRepository.save(vacancy);
            log.info(VACANCY_WITH_ID_APPROVED_BY_USER, vacancyId);
            // TODO отправка на почту.
        }
        else{
            vacancy.setStatus(REJECTED_BY_USER);
            vacancyRepository.save(vacancy);
            log.info(VACANCY_WITH_ID_REJECTED_BY_USER, vacancyId);
        }

    }
}
