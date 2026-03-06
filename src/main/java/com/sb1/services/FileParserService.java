package com.sb1.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Service
public class FileParserService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public String extractText(MultipartFile file) {

        if (Objects.isNull(file)) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds limit (5MB)");
        }

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        log.info("Parsing resume file: {}, contentType={}", fileName, contentType);

        try {
            if (isPdf(fileName, contentType)) {
                return parsePdf(file);
            }

            if (isDocx(fileName, contentType)) {
                return parseDocx(file);
            }

            if (isTxt(fileName, contentType)) {
                return parseTxt(file);
            }

            throw new IllegalArgumentException("Unsupported file type: " + contentType);

        } catch (Exception e) {
            log.error("Failed to parse file {}", fileName, e);
            throw new RuntimeException("Resume parsing failed", e);
        }
    }

    private boolean isPdf(String fileName, String contentType) {
        return contentType != null && contentType.equalsIgnoreCase("application/pdf")
                || fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }

    private boolean isDocx(String fileName, String contentType) {
        return contentType != null && contentType.equalsIgnoreCase(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || fileName != null && fileName.toLowerCase().endsWith(".docx");
    }

    private boolean isTxt(String fileName, String contentType) {
        return contentType != null && contentType.equalsIgnoreCase("text/plain")
                || fileName != null && fileName.toLowerCase().endsWith(".txt");
    }

    private String parsePdf(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             PDDocument document = PDDocument.load(is)) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            return stripper.getText(document);
        }
    }

    private String parseDocx(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            return extractor.getText();
        }
    }

    private String parseTxt(MultipartFile file) throws Exception {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }

        return sb.toString();
    }
}
