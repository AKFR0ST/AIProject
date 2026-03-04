package com.sb1.models.hh;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@ToString(exclude = "attachment")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 500)
    private String name;             // Имя кандидата / заголовок резюме

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String city;             // Город проживания

    @Column(length = 1000)
    private String profession;       // Позиция/специализация

    @Column(columnDefinition = "TEXT")
    private String skills;           // Краткий список навыков (текст, может быть JSON)

    @Column(columnDefinition = "TEXT")
    private String experience;       // Опыт работы (текст / структурированно JSON)

    @Column(columnDefinition = "TEXT")
    private String education;        // Образование (текст / структурированно JSON)

    @Column(columnDefinition = "TEXT")
    private String languages;        // Языки (текст / JSON)

    @Column(name = "attachment", columnDefinition = "bytea")
    private byte[] attachment; // здесь будет pdf / docx файл для письма

    @Column(length = 255, name = "attachment_name")
    private String attachmentName; // имя файла (resume.pdf)

}