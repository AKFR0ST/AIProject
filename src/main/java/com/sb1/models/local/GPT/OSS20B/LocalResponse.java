package com.sb1.models.local.GPT.OSS20B;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalResponse {
    @JsonProperty("model_instance_id")
    private String modelInstanceId;
    @JsonProperty("output")
    private ArrayList<LocalOutput> output;
    @JsonProperty("stats")
    LocalOutputStats stats;
    @JsonProperty("response_id ")
    String response_id;

}
