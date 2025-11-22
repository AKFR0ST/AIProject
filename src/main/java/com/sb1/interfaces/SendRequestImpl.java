package com.sb1.interfaces;

import com.sb1.enums.NNServices;
import com.sb1.services.GigaChatAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendRequestImpl implements SendRequest {
    public static final String MODEL_INCORRECT = "Некорректная модель";
    @Autowired
    GigaChatAPIService gigaChatAPIService;

    @Override
    public String sendTextToTextRequest(String role, String text, NNServices nnService) {
        switch (nnService) {
            case GIGA_CHAT : return gigaChatAPIService.textToTextRequest(role, text);

            default: return MODEL_INCORRECT;
        }
    }

    @Override
    public String sendImageToTextRequest(String role, String request, byte[] image, NNServices nnService) {
        switch (nnService) {
            case GIGA_CHAT : return gigaChatAPIService.imageToTextRequest(role, request, image);

            default: return MODEL_INCORRECT;
        }
    }
}
