package com.sb1.dto;

import lombok.Data;

@Data
public class HhVacancyDetailDto {

    private String id;
    private String description;
    private SalaryDto salary;
    private ContactsDto contacts;
}
