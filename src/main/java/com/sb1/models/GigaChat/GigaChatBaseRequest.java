package com.sb1.models.GigaChat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class GigaChatBaseRequest {
    @JsonProperty("model")
    private String model;
    @JsonProperty("stream")
    private Boolean stream;
    @JsonProperty("update_interval")
    private Integer updateInterval;
    @JsonProperty("messages")
    private ArrayList<GigaChatRequestMessage> messages;
}
