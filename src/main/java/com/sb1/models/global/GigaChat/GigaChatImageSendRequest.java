package com.sb1.models.global.GigaChat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GigaChatImageSendRequest {
    @JsonProperty("file")
    private byte[] image;
    @JsonProperty("purpose")
    private String purpose;
}
