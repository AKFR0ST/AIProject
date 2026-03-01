package com.sb1.mappers;

import com.sb1.dto.ResumeLlmDto;
import com.sb1.models.hh.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ResumeMapper {

    @Mapping(target = "attachment", ignore = true)
    @Mapping(target = "attachmentName", ignore = true)
    Resume fromLlmDto(ResumeLlmDto resumeLlmDto);
}
