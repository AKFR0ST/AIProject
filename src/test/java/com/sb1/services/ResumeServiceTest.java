package com.sb1.services;

import com.sb1.dto.ResumeLlmDto;
import com.sb1.enums.LLMServices;
import com.sb1.interfaces.LLMInterfaceImpl;
import com.sb1.mappers.ResumeMapper;
import com.sb1.models.hh.Resume;
import com.sb1.repositories.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private FileParserService fileParserService;

    @Mock
    private LLMInterfaceImpl llmClient;

    @Mock
    private ResumeLlmParser resumeLlmParser;

    @Mock
    private ResumeMapper resumeMapper;

    @InjectMocks
    private ResumeService resumeService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(resumeService, "llmDefault", "GPT_OSS20B");
    }

    @Test
    void addNewResume_shouldParseAndSaveResume() throws IOException {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        String parsedText = "Java developer with 5 years experience";
        String llmResponse = "{json}";

        ResumeLlmDto dto = new ResumeLlmDto();
        Resume resume = new Resume();
        resume.setId(1L);

        when(fileParserService.extractText(file)).thenReturn(parsedText);

        when(llmClient.sendTextToTextRequest(any(), any(), any()))
                .thenReturn(llmResponse);

        when(resumeLlmParser.parse(llmResponse))
                .thenReturn(dto);

        when(resumeMapper.fromLlmDto(dto))
                .thenReturn(new Resume());

        when(resumeRepository.save(any()))
                .thenReturn(resume);

        resumeService.addNewResume(file);

        verify(fileParserService).extractText(file);

        verify(llmClient).sendTextToTextRequest(
                any(),
                contains(parsedText),
                eq(LLMServices.GPT_OSS20B)
        );

        verify(resumeLlmParser).parse(llmResponse);

        verify(resumeMapper).fromLlmDto(dto);

        verify(resumeRepository).save(any(Resume.class));
    }
}