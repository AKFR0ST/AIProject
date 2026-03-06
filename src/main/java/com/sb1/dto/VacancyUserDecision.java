package com.sb1.dto;

import com.sb1.enums.UserDecision;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyUserDecision{
        Long vacancyId;
        UserDecision decision;
}
