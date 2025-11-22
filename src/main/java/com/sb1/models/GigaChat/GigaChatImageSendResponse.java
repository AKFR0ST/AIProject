package com.sb1.models.GigaChat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class GigaChatImageSendResponse {
    @JsonProperty("bytes")
    Integer bytes;
    @JsonProperty("created_at")
    Date createdAt;
    @JsonProperty("filename")
    String filename;
    @JsonProperty("id")
    UUID id;
    @JsonProperty("object")
    String object;
    @JsonProperty("purpose")
    String purpose;
    @JsonProperty("access_policy")
    String accessPolicy;
}
