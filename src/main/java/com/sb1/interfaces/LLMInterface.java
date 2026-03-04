package com.sb1.interfaces;

import com.sb1.enums.LLMServices;

public interface LLMInterface {
    String sendTextToTextRequest(String role, String text, LLMServices nnService);
    String sendImageToTextRequest(String role, String request, byte[] image, LLMServices nnService);
}
