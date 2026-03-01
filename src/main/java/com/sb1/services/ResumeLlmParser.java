package com.sb1.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sb1.dto.ResumeLlmDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResumeLlmParser {

    private final ObjectMapper objectMapper;

    public ResumeLlmDto parse(String llmResponse) {
        try {
            return objectMapper.readValue(llmResponse, ResumeLlmDto.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse LLM JSON response", e);
        }
    }
}
