package com.sb1.models.local.GPT.OSS20B;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalOutputStats {
    @JsonProperty("model_instance_id")
    int inputTokens;
    @JsonProperty("total_output_tokens")
    int totalOutputTokens;
    @JsonProperty("reasoning_output_tokens")
    int reasoningOutputTokens;
    @JsonProperty("tokens_per_second")
    double tokensPerSecond;
    @JsonProperty("time_to_first_token_seconds")
    double timeToFirstTokenSeconds;
}
