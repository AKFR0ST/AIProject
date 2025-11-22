package com.sb1.services;

import com.sb1.clients.GigaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GigaChatAPIService {

    @Autowired
    private GigaChatClient gigaChatClient;

    public String textToTextRequest(String role, String text) {
        return gigaChatClient.gigaChatTextToTextRequest(role, text);
    }

    public String imageToTextRequest(String role, String request, byte[] image) {
        return gigaChatClient.gigaChatImageToTextRequest(role, request, image);
    }
}
