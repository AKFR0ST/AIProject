package com.sb1.dto;

import com.sb1.enums.UserDecision;

public record VacancyUserDecision(
        Long vacancyId,
        UserDecision decision
) {}
