package com.sb1.models.hh;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
//import org.springframework.data.annotation.Id;
import jakarta.persistence.Id;
//import org.springframework.data.relational.core.mapping.Table;
//import org.springframework.data.relational.core.mapping.Table;

//@Table(name = "vacancies")
@Data
@Entity
@Table(name = "vacancies")
public class Vacancy {

    @Id
    private String hhId;

    private String name;
    private String employer;
    private String url;
    private Integer salaryFrom;
    private Integer salaryTo;
    private String publishedAt;

    // getters/setters
}
