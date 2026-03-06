package com.sb1.services;

import com.sb1.dto.ResumeLlmDto;
import com.sb1.enums.LLMServices;
import com.sb1.interfaces.LLMInterfaceImpl;
import com.sb1.mappers.ResumeMapper;
import com.sb1.models.hh.Resume;
import com.sb1.repositories.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.sb1.constants.LlmPrompts.RESUME_PARSER_SYSTEM;
import static com.sb1.constants.LlmPrompts.RESUME_PARSER_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final FileParserService fileParserService;
    private final LLMInterfaceImpl llmClient;
    private final ResumeLlmParser resumeLlmParser;
    private final ResumeMapper resumeMapper;

    @Value("${general.llm.default}")
    private String llmDefault;

    @Transactional
    public void addNewResume(MultipartFile file) throws IOException {

        log.info("Start processing resume file: {}", file.getOriginalFilename());

        String text = fileParserService.extractText(file);

        String responseFromLlm = llmClient.sendTextToTextRequest(
                RESUME_PARSER_SYSTEM,
                RESUME_PARSER_USER.formatted(text),
                LLMServices.valueOf(llmDefault)
        );

        ResumeLlmDto resumeLlmDto = resumeLlmParser.parse(responseFromLlm);


        Resume resume = resumeMapper.fromLlmDto(resumeLlmDto);

        resume.setAttachment(file.getBytes());
        resume.setAttachmentName(file.getOriginalFilename());

        Resume saved = resumeRepository.save(resume);

        log.info("Resume saved with id {}", saved.getId());

    }

}
