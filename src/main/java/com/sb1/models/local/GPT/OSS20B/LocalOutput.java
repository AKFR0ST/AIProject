package com.sb1.models.local.GPT.OSS20B;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalOutput {
    @JsonProperty("Message")
    LocalMessage message;


}
