package com.sb1.clients;

import com.sb1.models.global.GigaChat.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sb1.constants.GigaChatConstants.*;
import static com.sb1.enums.GigaChatModels.GIGACHAT_2_MAX;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.MULTIPART_FORM_DATA;

@Component
public class GigaChatClient {

    private final String authKey;
    private final String authUrl;
    private final String baseUrl;
    private final String filesUrl;

    private RestClient authRestClient;
    private RestClient baseRestClient;
    private RestClient imageRestClient;
    private String token;

    public GigaChatClient(
            @Value("${gigachat.auth.url}") String authUrl,
            @Value("${gigachat.autorization.key}") String authKey,
            @Value("${gigachat.base.url}") String baseUrl,
            @Value("${gigachat.files.url}") String filesUrl

    ) {
        this.authKey = authKey;
        this.authUrl = authUrl;
        this.baseUrl = baseUrl;
        this.filesUrl = filesUrl;
        System.out.println(authUrl);

        updateAuthClient();
        updateBaseClient();
        updateImageClient();
    }

    private void updateAuthClient() {
        authRestClient = RestClient.builder()
                .baseUrl(authUrl)
                .defaultHeader(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED)
                .defaultHeader(RQ_UID, UUID.randomUUID().toString())
                .defaultHeader(AUTHORIZATION, BEARER + authKey)
                .build();
    }

    private void updateBaseClient() {
        baseRestClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON)
                .defaultHeader(ACCEPT, APPLICATION_JSON)
                .defaultHeader(AUTHORIZATION, BEARER + token)
                .build();
    }

    private void updateImageClient() {
        imageRestClient = RestClient.builder()
                .baseUrl(filesUrl)
                .defaultHeader(CONTENT_TYPE, MULTIPART_FORM_DATA)
                .defaultHeader(AUTHORIZATION, BEARER + token)
                .build();
    }


    public GigaChatImageSendResponse loadImage(byte[] image) {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(image) {
            @Override
            public String getFilename() {
                return "image.jpg";
            }
        });
        body.add("purpose", "general");

        try {
            return imageRestClient
                    .post()
                    .body(body)
                    .retrieve()
                    .body(GigaChatImageSendResponse.class);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                updateToken();
                updateImageClient();
                return imageRestClient.post()
                        .body(body)
                        .retrieve()
                        .body(GigaChatImageSendResponse.class);
            } else {
                throw e;
            }
        }
    }

    private GigaChatBaseResponse executeWithRetryOnUnauthenticated(GigaChatBaseRequest requestChat) {
        try {
            return baseRestClient.post()
                    .body(requestChat)
                    .retrieve()
                    .body(GigaChatBaseResponse.class);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                updateToken();
                updateBaseClient();
                return baseRestClient.post()
                        .body(requestChat)
                        .retrieve()
                        .body(GigaChatBaseResponse.class);
            } else {
                throw e;
            }
        }
    }

    public String gigaChatImageToTextRequest(String role, String request, byte[] attachment) {
        GigaChatImageSendResponse image = loadImage(attachment);
        return gigaChatTextToTextRequestWithAttachments(role, request, List.of(image.getId().toString()));
    }

    public String gigaChatTextToTextRequest(String role, String request){
        return gigaChatTextToTextRequestWithAttachments(role, request, null);
    }

    private String gigaChatTextToTextRequestWithAttachments(String role, String request, List<String> attachments) {

        ArrayList<GigaChatRequestMessage> messages = new ArrayList<>();

        if (!role.isEmpty()) {
            GigaChatRequestMessage roleMessage = GigaChatRequestMessage.builder()
                    .role(SYSTEM)
                    .content(role)
                    .build();
            messages.add(roleMessage);
        }

        GigaChatRequestMessage textMessage = GigaChatRequestMessage.builder()
                .role(USER)
                .content(request)
                .attachments(attachments)
                .build();
        messages.add(textMessage);

        GigaChatBaseRequest requestChat = GigaChatBaseRequest.builder()
                .model(GIGACHAT_2_MAX.getTitle())
                .stream(false)
                .updateInterval(0)
                .messages(messages)
                .build();

        GigaChatBaseResponse gigaChatBaseResponse = executeWithRetryOnUnauthenticated(requestChat);

        return gigaChatBaseResponse.getGigaChatChoices().getFirst().getRequestMessage().getContent();
    }

    public void printToken() {
        updateToken();
        System.out.println("GigaChat token: " + token);
    }

    private void updateToken() {
        GigaChatResponseToken result = authRestClient.post().body(SCOPE_GIGACHAT_API_PERS).retrieve().body(GigaChatResponseToken.class);

        assert result != null;
        token = result.getToken();
    }
}
