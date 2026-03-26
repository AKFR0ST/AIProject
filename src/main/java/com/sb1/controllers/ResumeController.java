package com.sb1.controllers;

import com.sb1.models.hh.Resume;
import com.sb1.repositories.ResumeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api/v1")
public class ResumeController {
    public ResumeController(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }
    ResumeRepository resumeRepository;

    @GetMapping("/resumes/{id}")
    public Resume getResumeById(@PathVariable Long id) {
        Optional<Resume> resume = resumeRepository.findById(id);
        return resume.orElseThrow();
    }

    @PostMapping("/resumes")
    void addResume(@RequestBody Resume resume) {
        resumeRepository.save(resume);
    }

}
