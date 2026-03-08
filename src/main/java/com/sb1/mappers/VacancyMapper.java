package com.sb1.mappers;

import com.sb1.dto.HhVacancyDetailDto;
import com.sb1.dto.HhVacancyDto;
import com.sb1.models.hh.Vacancy;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface VacancyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hhId", source = "id")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "url", source = "alternateUrl")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "contactName", ignore = true)
    @Mapping(target = "employer", source = "employer.name")
    @Mapping(target = "salaryFrom", source = "salary.from")
    @Mapping(target = "salaryTo", source = "salary.to")
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "coverLetter", ignore = true)
    Vacancy toEntity(HhVacancyDto dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "contactName", source = "contacts.name")
    @Mapping(target = "email", source = "contacts.email")
    @Mapping(target = "salaryFrom", source = "salary.from")
    @Mapping(target = "salaryTo", source = "salary.to")
    @Mapping(target = "description", source = "description")
    void updateFromDetail(HhVacancyDetailDto dto, @MappingTarget Vacancy vacancy);
}

