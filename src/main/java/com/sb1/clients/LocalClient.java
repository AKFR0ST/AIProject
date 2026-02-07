package com.sb1.clients;

import com.sb1.models.local.GPT.OSS20B.LocalRequest;
import com.sb1.models.local.GPT.OSS20B.LocalResponse;
import org.apache.http.HttpHost;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static com.sb1.constants.GigaChatConstants.*;
import static com.sb1.constants.GigaChatConstants.BEARER;

@Component
public class LocalClient {

    private String host;
    private int port;
    private String baseUrl;

    private RestClient restClient;

    private void updateClient(){
        restClient = RestClient.builder()
                .baseUrl("http://127.0.0.1:1234/api/v1/chat")
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON)
                .defaultHeader(AUTHORIZATION, BEARER + "sk-lm-9NrfMXYn:Xky2oGpbc5y0eABn1lHg")
                .build();
    }

    public void localTextToTextRequest(String textOfRequest){
        LocalRequest localRequest = LocalRequest.builder()
                .model("openai/gpt-oss-20b")
                .input(textOfRequest)
                .build();

        updateClient();

        LocalResponse response = restClient.post()
                .body(localRequest)
                .retrieve()
                .body(LocalResponse.class);

        System.out.println(response);
    }
}
