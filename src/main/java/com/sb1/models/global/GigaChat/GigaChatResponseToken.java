package com.sb1.models.global.GigaChat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class GigaChatResponseToken {
    @JsonProperty("access_token")
    private String token;
    @JsonProperty("expires_at")
    private Date expiration;

}
