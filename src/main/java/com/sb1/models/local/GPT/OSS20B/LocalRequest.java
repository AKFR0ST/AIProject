package com.sb1.models.local.GPT.OSS20B;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocalRequest {
    private String model;
    private String input;
//    private ArrayList<LocalInput> input;
}
