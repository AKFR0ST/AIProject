package com.sb1.interfaces;

import com.sb1.enums.LLMServices;
import com.sb1.services.GigaChatAPIService;
import com.sb1.services.LocalAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LLMInterfaceImpl implements LLMInterface {
    public static final String MODEL_INCORRECT = "Некорректная модель";
    @Autowired
    GigaChatAPIService gigaChatAPIService;
    @Autowired
    LocalAPIService localAPIService;


    @Override
    public String sendTextToTextRequest(String role, String text, LLMServices nnService) {
        switch (nnService) {
            case GIGA_CHAT : return gigaChatAPIService.textToTextRequest(role, text);
            case GPT_OSS20B: return localAPIService.textToTextRequest(role, text);

            default: return MODEL_INCORRECT;
        }
    }

    @Override
    public String sendImageToTextRequest(String role, String request, byte[] image, LLMServices nnService) {
        switch (nnService) {
            case GIGA_CHAT : return gigaChatAPIService.imageToTextRequest(role, request, image);

            default: return MODEL_INCORRECT;
        }
    }
}
