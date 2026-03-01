package com.sb1.models.hh;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "resumes")
@Data
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Lob
    private String skills;           // Краткий список навыков (текст, может быть JSON)

    @Lob
    private String experience;       // Опыт работы (текст / структурированно JSON)

    @Lob
    private String education;        // Образование (текст / структурированно JSON)

    @Lob
    private String languages;        // Языки (текст / JSON)

    @Lob
    private byte[] attachment; // здесь будет pdf / docx файл для письма

    @Column(length = 255)
    private String attachmentName; // имя файла (resume.pdf)

}