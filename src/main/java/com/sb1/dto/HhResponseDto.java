package com.sb1.dto;

import lombok.Data;

import java.util.List;

@Data
public class HhResponseDto {
    private List<HhVacancyDto> items;
}
