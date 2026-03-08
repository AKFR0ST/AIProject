# AIProject

AIProject is a **Spring Boot** application that automates the process of analyzing job vacancies and interacting with Large Language Models (LLM).  
The system retrieves vacancies from the **HeadHunter API**, processes them with AI models (GigaChat or local LLM), scores them, and helps generate responses such as cover letters.

The project also integrates with **Telegram** and **Kafka** for asynchronous processing and user interaction.

---

## Features

- Integration with **HeadHunter API** for retrieving vacancies
- AI processing using **GigaChat** or local **LLM**
- Resume parsing and vacancy analysis
- Automated **cover letter generation**
- **Kafka-based** asynchronous processing pipeline
- **Telegram bot** for user interaction
- Vacancy scoring and filtering

---

## Tech Stack

- **Java 21**
- **Spring Boot**
- **Apache Kafka**
- **Maven**
- **HeadHunter API**
- **GigaChat API**
- **Telegram Bot API**
- Local LLM support

---

## Processing Pipeline

**Typical vacancy processing flow:**

1. Vacancies are retrieved from HeadHunter API.
2. Vacancy data is stored in the database.
3. Kafka events trigger processing.
4. LLM analyzes vacancy relevance.
5. The system generates:
6. Vacancy score
7. Cover letter
8. User interaction happens through Telegram Bot.
