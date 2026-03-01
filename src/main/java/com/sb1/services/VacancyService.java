package com.sb1.services;

import com.sb1.clients.GigaChatClient;
import com.sb1.clients.HhClient;
import com.sb1.dto.HhResponseDto;
import com.sb1.dto.HhVacancyDetailDto;
import com.sb1.dto.HhVacancyDto;
import com.sb1.enums.VacancyStatus;
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

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {

    private final HhClient hhClient;
    private final VacancyRepository vacancyRepository;
    private final ResumeRepository resumeRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final GigaChatClient gigaChatClient;

    @Autowired
    private VacancyMapper vacancyMapper;

    @Value("${hh.search.text}")
    private String text;

    @Value("${hh.search.area}")
    private String area;

    @Value("${hh.search.per-page}")
    private int perPage;

    @Scheduled(fixedDelayString = "${hh.search.delay}") // каждые 10 минут
    public void fetchAndStoreNewVacancy() {

        HhResponseDto responseDto = hhClient.searchNewVacancies(text, area, perPage);

        if (Objects.isNull(responseDto) || Objects.isNull(responseDto.getItems())) {
            return;
        }

        for (HhVacancyDto dto : responseDto.getItems()) {

            if (!vacancyRepository.existsById(dto.getId())) {

                Vacancy vacancy = vacancyMapper.toEntity(dto);
                vacancyRepository.save(vacancy);
                log.info("NEW vacancy with id {} saved: {}", vacancy.getId(), vacancy.getName());

                kafkaTemplate.send("topic-1", vacancy.getId())
                        .whenComplete((result, ex) -> {
                            if (Objects.isNull(ex)) {
                                log.info("New vacancy message sent to Kafka for vacancy {}", vacancy.getId());
                            } else {
                                log.error("Failed to send message for vacancy {}", vacancy.getId(), ex);
                            }
                        });
            }
        }
    }

    public void enrichmentVacancy(Long id){
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(id))
                .orElseThrow();

        HhVacancyDetailDto detail = hhClient.getVacancyById(vacancy.getHhId());

        vacancyMapper.updateFromDetail(detail, vacancy);

        vacancy.setStatus(VacancyStatus.DETAILED);

        vacancyRepository.save(vacancy);

        log.info("Vacancy with id {} detailed: {}", vacancy.getId(), vacancy.getName());

        kafkaTemplate.send("topic-2", vacancy.getId())
                .whenComplete((result, ex) -> {
                    if (Objects.isNull(ex)) {
                        log.info("Message with status {} sent to Kafka for vacancy {}", vacancy.getStatus(), vacancy.getId());
                    } else {
                        log.error("Failed to send message for vacancy {}", vacancy.getId(), ex);
                    }
                });
    }

    public void createCoverLetter(Long id) {
        Vacancy vacancy = vacancyRepository.findById(String.valueOf(id))
                .orElseThrow();

        //  Получить резюме
        Resume resume = resumeRepository.findById(id).orElseThrow();


        //  Передать в ЛЛМ и записать ответ в базу
        String coverLetter = gigaChatClient.gigaChatTextToTextRequest("Профессиональный рекрутер", "Подготовь короткое сопроводительное письмо на следующую вакансию: " + vacancy.toString() + "Под это резюме" + resume.toString());


    }

}
