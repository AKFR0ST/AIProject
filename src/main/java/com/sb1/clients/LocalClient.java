package com.sb1.clients;

import com.sb1.models.local.GPT.OSS20B.LocalRequest;
import com.sb1.models.local.GPT.OSS20B.LocalResponse;
import org.apache.http.HttpHost;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static com.sb1.constants.GigaChatConstants.*;
import static com.sb1.constants.GigaChatConstants.BEARER;

@Component
public class LocalClient {

    private String host;
    private int port;
    private String baseUrl;

    WebClient webClient;

    private void updateClient(){
        webClient = WebClient.builder()
                .baseUrl("http://127.0.0.1:1234/")
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON)
                .defaultHeader(ACCEPT, APPLICATION_JSON)
                .defaultHeader(AUTHORIZATION, BEARER + "sk-lm-9NrfMXYn:Xky2oGpbc5y0eABn1lHg")
                .build();
    }

    public String localTextToTextRequest(String textOfRequest){
        LocalRequest localRequest = LocalRequest.builder()
                .model("openai/gpt-oss-20b")
                .input(textOfRequest)
                .stream(false)
                .build();

        updateClient();
        LocalResponse response = null;
    try {
        response = webClient.post()
                .uri("/api/v1/chat")
                .bodyValue(localRequest)
                .retrieve()
                .bodyToMono(LocalResponse.class)
                .block(Duration.ofSeconds(60));


        System.out.println(response);
    } catch (RestClientException e) {
        e.printStackTrace();
    }
        return response.getOutput().getFirst().getMessage().getContent();
    }
}
