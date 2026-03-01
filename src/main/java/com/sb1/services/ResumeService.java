package com.sb1.services;

import com.sb1.clients.GigaChatClient;
import com.sb1.dto.ResumeLlmDto;
import com.sb1.mappers.ResumeMapper;
import com.sb1.models.hh.Resume;
import com.sb1.repositories.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final FileParserService fileParserService;
    private final GigaChatClient llmClient;
    private final ResumeLlmParser resumeLlmParser;
    private final ResumeMapper resumeMapper;

    @Transactional
    public Resume addNewResume(MultipartFile file) throws IOException {

        log.info("Start processing resume file: {}", file.getOriginalFilename());

        // 1. Извлекаем текст
        String text = fileParserService.extractText(file);

        // 2. Отправляем в LLM
        String responseFromLlm = llmClient.gigaChatTextToTextRequest("Ты HR-система, которая извлекает структурированные данные из резюме", "Проанализируй текст резюме и верни строго JSON в формате:\n" +
                "\n" +
                "{\n" +
                "  \"name\": \"\",\n" +
                "  \"email\": \"\",\n" +
                "  \"phone\": \"\",\n" +
                "  \"city\": \"\",\n" +
                "  \"profession\": \"\",\n" +
                "  \"skills\": \"\",\n" +
                "  \"experience\": \"\",\n" +
                "  \"education\": \"\",\n" +
                "  \"languages\": \"\"\n" +
                "}\n" +
                "\n" +
                "Верни ТОЛЬКО валидный JSON. Без комментариев. Без пояснений. " + text);

        ResumeLlmDto resumeLlmDto = resumeLlmParser.parse(responseFromLlm);


        // 3. Мапим в entity
        Resume resume = resumeMapper.fromLlmDto(resumeLlmDto);

        // 4. Сохраняем файл
        resume.setAttachment(file.getBytes());
        resume.setAttachmentName(file.getOriginalFilename());

        // 5. Сохраняем в БД
        Resume saved = resumeRepository.save(resume);

        log.info("Resume saved with id {}", saved.getId());

        return saved;
    }

}
