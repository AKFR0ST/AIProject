package com.sb1.clients;

import com.sb1.dto.HhResponseDto;
import com.sb1.dto.HhVacancyDetailDto;
import com.sb1.dto.HhVacancyDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class HhClient {

    private final WebClient webClient;

    public HhClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.hh.ru")
                .defaultHeader("User-Agent", "hh-microservice")
                .build();
    }

    public List<HhVacancyDto> searchAllVacancies(String text, int codeOfRole, String experience, String ordering, int perPage, String dateFrom) {
        List<HhVacancyDto> allVacancies = new ArrayList<>();
        int page = 0;

        while (true) {
            int finalPage = page;
            HhResponseDto response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/vacancies")
                            .queryParam("text", text)
                            .queryParam("professional_role", codeOfRole)
                            // .queryParam("experience", experience)
                            .queryParam("order_by", ordering)
                            .queryParam("per_page", perPage)
                            .queryParam("page", finalPage)
                            .queryParam("date_from", dateFrom)
                            .build())
                    .retrieve()
                    .bodyToMono(HhResponseDto.class)
                    .block();

            if (Objects.isNull(response) || Objects.isNull(response.getItems())  || response.getItems().isEmpty()) {
                break;
            }

            allVacancies.addAll(response.getItems());
            page++;
        }

        return allVacancies;
    }

    @Retry(name = "hhApi")
    @CircuitBreaker(name = "hhApi", fallbackMethod = "fallbackVacancy")
    public HhVacancyDetailDto getVacancyById(String vacancyId) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/vacancies/{id}")
                        .build(vacancyId))
                .retrieve()
                .bodyToMono(HhVacancyDetailDto.class)
                .block();
    }

    public HhVacancyDetailDto fallbackVacancy(String vacancyId, Throwable ex) {
        log.warn("HH API unavailable for vacancy {}", vacancyId);
        return null;
    }
}
