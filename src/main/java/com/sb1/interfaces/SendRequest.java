package com.sb1.interfaces;

import com.sb1.enums.NNServices;

public interface SendRequest {
    String sendTextToTextRequest(String role, String text, NNServices nnService);
    String sendImageToTextRequest(String role, String request, byte[] image, NNServices nnService);
}
