package com.sb1.services;

import com.sb1.clients.HhClient;
import com.sb1.dto.HhResponseDto;
import com.sb1.dto.HhVacancyDetailDto;
import com.sb1.dto.HhVacancyDto;
import com.sb1.mappers.VacancyMapper;
import com.sb1.models.hh.Vacancy;
import com.sb1.repositories.VacancyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {

    private final HhClient hhClient;
    private final VacancyRepository repository;
    private final KafkaTemplate<String, Vacancy> kafkaTemplate;

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

        if (responseDto == null || responseDto.getItems() == null) {
            return;
        }

        for (HhVacancyDto dto : responseDto.getItems()) {

            if (!repository.existsById(dto.getId())) {

                Vacancy vacancy = vacancyMapper.toEntity(dto);
                repository.save(vacancy);

                //  TODO Send to kafka
                kafkaTemplate.send("topic-1", vacancy.getId().toString(), vacancy)
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.info("Message sent to Kafka for vacancy {}", vacancy.getId());
                            } else {
                                log.error("Failed to send message for vacancy {}", vacancy.getId(), ex);
                            }
                        });

                log.info("NEW vacancy with id {} saved: {}", vacancy.getId(), vacancy.getName());
//                enrichmentVacancy(vacancy.getId());
            }
        }
    }

    public void enrichmentVacancy(Long id){
        Vacancy vacancy = repository.findById(String.valueOf(id))
                .orElseThrow();

        HhVacancyDetailDto detail = hhClient.getVacancyById(vacancy.getHhId());

        vacancyMapper.updateFromDetail(detail, vacancy);

        repository.save(vacancy);
        log.info("Vacancy with id {} detalized: {}", vacancy.getId(), vacancy.getName());
    }
}
