package com.sb1.controllers;

import com.sb1.models.hh.Vacancy;
import com.sb1.repositories.VacancyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/api/v1")
public class VacancyController {
    public VacancyController(VacancyRepository vacancyRepository) {
        this.vacancyRepository = vacancyRepository;
    }
    VacancyRepository vacancyRepository;

    @GetMapping("/vacancy/{id}")
    public Vacancy getVacancy(@PathVariable String id) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(id);
        return vacancy.orElseThrow();
    }
}
