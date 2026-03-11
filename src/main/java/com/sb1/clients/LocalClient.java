package com.sb1.clients;

import com.sb1.models.local.GPT.OSS20B.LocalRequest;
import com.sb1.models.local.GPT.OSS20B.LocalResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static com.sb1.constants.GigaChatConstants.*;
import static com.sb1.constants.GigaChatConstants.BEARER;

@Component
public class LocalClient {

    private final WebClient webClient;

    public LocalClient(
            @Value("${local.base.url}") String baseUrl,
            @Value("${local.bearer.token}")String bearerToken
    ){
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON)
                .defaultHeader(ACCEPT, APPLICATION_JSON)
                .defaultHeader(AUTHORIZATION, BEARER + bearerToken)
                .build();
    }

    public String localTextToTextRequest(String prompt, String textOfRequest){
        LocalRequest localRequest = LocalRequest.builder()
                .model("openai/gpt-oss-20b")
                .input(textOfRequest)
                .systemPrompt(prompt)
                .stream(false)
                .build();

        LocalResponse response = null;
    try {
        response = webClient.post()
                .bodyValue(localRequest)
                .retrieve()
                .bodyToMono(LocalResponse.class)
                .block(Duration.ofSeconds(60));


        System.out.println(response);
    } catch (RestClientException e) {
        e.printStackTrace();
    }
        return response.getOutput().get(1).getContent();
    }
}
