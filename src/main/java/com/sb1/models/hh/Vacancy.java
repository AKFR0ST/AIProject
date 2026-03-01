package com.sb1.models.hh;


import com.sb1.enums.VacancyStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vacancies")

public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "hh_id")
    private String hhId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VacancyStatus status;
    private String name;
    private String employer;
    @Lob
    private String description;
    private String url;
    @Column(name = "email")
    private String email;
    @Column(name = "contact_name")
    private String contactName;
    @Column(name = "salary_from")
    private Integer salaryFrom;
    @Column(name = "salary_to")
    private Integer salaryTo;
    @Column(name = "published_at")
    private String publishedAt;
    @Lob
    private String coverLetter;
}
