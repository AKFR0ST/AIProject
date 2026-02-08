package com.sb1.models.local.GPT.OSS20B;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocalMessage {
    @JsonProperty("type")
    String type;
    @JsonProperty("content")
    String content;
}
