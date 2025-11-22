package com.sb1.models.GigaChat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class GigaChatBaseResponse {
    @JsonProperty("choices")
    private ArrayList<GigaChatChoice> gigaChatChoices;
    @JsonProperty("created")
    private Integer created;
    @JsonProperty("model")
    private String model;
    @JsonProperty("object")
    private String object;
    @JsonProperty("usage")
    private GigaChatUsage usage;
}
