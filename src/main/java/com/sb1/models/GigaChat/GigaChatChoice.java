package com.sb1.models.GigaChat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GigaChatChoice {
    @JsonProperty("message")
    GigaChatRequestMessage requestMessage;
    @JsonProperty("index")
    Integer index;
    @JsonProperty("finish_reason")
    String finishReason;
}
