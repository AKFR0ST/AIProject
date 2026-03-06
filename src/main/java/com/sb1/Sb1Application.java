package com.sb1;

import com.sb1.services.ResumeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@EnableScheduling
@SpringBootApplication
public class Sb1Application {

    public static void main(String[] args) throws IOException, TelegramApiException {
        ApplicationContext context = SpringApplication.run(Sb1Application.class, args);

        Path path = Path.of("C:\\Users\\FROST\\Desktop\\java-jun-KovalevAM\\force\\KovalevAM(Java Middle).docx");
        MultipartFile multipartFile = new MockMultipartFile(
                "file",                              // имя параметра
                "KovalevAM(Java Middle).docx",             // имя файла
                Files.probeContentType(path),        // MIME тип
                Files.readAllBytes(path)             // содержимое
        );

        ResumeService resumeService = context.getBean(ResumeService.class);
        resumeService.addNewResume(multipartFile);

    }}

