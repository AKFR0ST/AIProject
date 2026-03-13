package com.sb1;

import com.sb1.services.ResumeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@EnableScheduling
@SpringBootApplication
public class Sb1Application {

    public static void main(String[] args) throws IOException, TelegramApiException {

        ApplicationContext context = SpringApplication.run(Sb1Application.class, args);

        ResumeService resumeService = context.getBean(ResumeService.class);
        resumeService.loadResumeFromPath();

    }}

