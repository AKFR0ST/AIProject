package com.sb1.services;

import com.sb1.clients.HhClient;
import com.sb1.dto.HhVacancyDetailDto;
import com.sb1.dto.HhVacancyDto;
import com.sb1.dto.VacancyUserDecision;
import com.sb1.enums.Senders;
import com.sb1.enums.UserDecision;
import com.sb1.interfaces.LLMInterfaceImpl;
import com.sb1.mappers.VacancyMapper;
import com.sb1.models.hh.Resume;
import com.sb1.models.hh.Vacancy;
import com.sb1.repositories.ResumeRepository;
import com.sb1.repositories.VacancyRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private HhClient hhClient;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private LLMInterfaceImpl llm;

    @Mock
    private HhApplyService hhApplyService;

    @Mock
    private VacancyMapper vacancyMapper;

    @InjectMocks
    private VacancyService vacancyService;


    @Test
    void fetchAndStoreNewVacancy_shouldSaveNewVacancyAndSendKafkaMessage() {

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(null);

        HhVacancyDto dto = new HhVacancyDto();
        dto.setId("123");

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);

        when(hhClient.searchAllVacancies(any(), anyInt(), any(), any(), anyInt(), any()))
                .thenReturn(List.of(dto));

        when(vacancyRepository.existsByHhId("123"))
                .thenReturn(false);

        when(vacancyMapper.toEntity(dto))
                .thenReturn(vacancy);

        when(kafkaTemplate.send(anyString(), any()))
                .thenReturn(future);

        vacancyService.fetchAndStoreNewVacancy();

        verify(vacancyRepository).save(vacancy);
        verify(kafkaTemplate).send(eq("topic-detailed"), eq(1L));
    }

    @Test
    void detailVacancy_shouldUpdateVacancyAndSendToScoring() {

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setHhId("hh123");

        HhVacancyDetailDto detail = new HhVacancyDetailDto();

        when(vacancyRepository.findById("1"))
                .thenReturn(Optional.of(vacancy));

        when(hhClient.getVacancyById("hh123"))
                .thenReturn(detail);

        SendResult<String, Object> sendResult =
                new SendResult<>(new ProducerRecord<>("topic-scoring", vacancy.getId()),
                        new RecordMetadata(null, 0, 0, 0L, 0L, 0, 0));
        when(kafkaTemplate.send(eq("topic-scoring"), eq(1L)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        vacancyService.detailVacancy(1L);

        verify(vacancyMapper).updateFromDetail(detail, vacancy);
        verify(vacancyRepository).save(vacancy);
        verify(kafkaTemplate).send(eq("topic-scoring"), eq(1L));
    }

    @Test
    void vacancyScoring_shouldSendToCoverLetterWhenScoreGood() {

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setDescription("Java dev");

        ReflectionTestUtils.setField(vacancyService, "llmDefault", "GPT_OSS20B");
        ReflectionTestUtils.setField(vacancyService, "minScore", 80);

        Resume resume = new Resume();

        when(vacancyRepository.findById("1"))
                .thenReturn(Optional.of(vacancy));

        when(resumeRepository.findById(1L))
                .thenReturn(Optional.of(resume));

        when(llm.sendTextToTextRequest(any(), any(), any()))
                .thenReturn("90");

        when(kafkaTemplate.send(eq("topic-coverLetter"), eq(1L)))
                .thenReturn(CompletableFuture.completedFuture(null));

        vacancyService.vacancyScoring(1L, 1L);

        verify(kafkaTemplate).send(eq("topic-coverLetter"), eq(1L));
        verify(vacancyRepository).save(vacancy);
    }

    @Test
    void vacancyScoring_shouldMarkBadScoring() {

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setDescription("Java dev");

        ReflectionTestUtils.setField(vacancyService, "llmDefault", "GPT_OSS20B");
        ReflectionTestUtils.setField(vacancyService, "minScore", 80);

        Resume resume = new Resume();

        when(vacancyRepository.findById("1"))
                .thenReturn(Optional.of(vacancy));

        when(resumeRepository.findById(1L))
                .thenReturn(Optional.of(resume));

        when(llm.sendTextToTextRequest(any(), any(), any()))
                .thenReturn("10");

        vacancyService.vacancyScoring(1L, 1L);

        verify(vacancyRepository).save(vacancy);
        verify(kafkaTemplate, never()).send(eq("topic-coverLetter"), any());
    }

    @Test
    void processingUserDecision_shouldApproveAndSendApply() {

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);

        ReflectionTestUtils.setField(vacancyService, "defaultSender", Senders.HH);

        VacancyUserDecision decision = new VacancyUserDecision();
        decision.setVacancyId(1L);
        decision.setDecision(UserDecision.APPROVE);

        when(vacancyRepository.findById("1"))
                .thenReturn(Optional.of(vacancy));

        vacancyService.processingUserDecision(decision);

        verify(vacancyRepository).save(vacancy);
        verify(hhApplyService).apply(1L); // теперь мок вызовется
    }

    @Test
    void processingUserDecision_shouldReject() {

        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);

        VacancyUserDecision decision = new VacancyUserDecision();
        decision.setVacancyId(1L);
        decision.setDecision(UserDecision.REJECT);

        when(vacancyRepository.findById("1"))
                .thenReturn(Optional.of(vacancy));

        vacancyService.processingUserDecision(decision);

        verify(vacancyRepository).save(vacancy);
        verify(hhApplyService, never()).apply(any());
    }
}