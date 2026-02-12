package com.sb1.models.local.GPT.OSS20B;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocalRequest {
    @JsonProperty("model")
    private String model;
    @JsonProperty("input")
    private String input;
    @JsonProperty("stream")
    private Boolean stream;
    @JsonProperty("system_prompt")
    private String systemPrompt;
//    private ArrayList<LocalInput> input;
}
