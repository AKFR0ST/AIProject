package com.sb1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HhVacancyDto {

    private String id;
    private String name;

    @JsonProperty("alternate_url")
    private String alternateUrl;

    @JsonProperty("published_at")
    private String publishedAt;

    private EmployerDto employer;
    private SalaryDto salary;
}
