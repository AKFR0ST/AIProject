package com.sb1.clients;

import com.sb1.dto.HhResponseDto;
import com.sb1.dto.HhVacancyDetailDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HhClient {

    private final WebClient webClient;

    public HhClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.hh.ru")
                .defaultHeader("User-Agent", "hh-microservice")
                .build();
    }

    public HhResponseDto searchNewVacancies(String text, String area, int codeOfRole, String experience, String ordering, int perPage, String dateFrom) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/vacancies")
                        .queryParam("text", text)
                        .queryParam("area", area)
                        .queryParam("professional_role", codeOfRole)
//                        .queryParam("professional_role", "124")  //  TODO Убрать и добавить поддержку нескольких ролей.
                        .queryParam("experience", experience)
                        .queryParam("order_by", ordering)
                        .queryParam("per_page", perPage)
                        .queryParam("date_from", dateFrom)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("HH API error: " + body))
                )
                .bodyToMono(HhResponseDto.class)
                .block();
    }

    public HhVacancyDetailDto getVacancyById(String vacancyId) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/vacancies/{id}")
                        .build(vacancyId))
                .retrieve()
                .bodyToMono(HhVacancyDetailDto.class)
                .block();
    }
}
