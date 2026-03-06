package com.sb1.clients;

import com.sb1.dto.VacancyUserDecision;
import com.sb1.enums.UserDecision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

import static com.sb1.enums.UserDecision.APPROVE;
import static com.sb1.enums.UserDecision.REJECT;

@Slf4j
@Service
@RequiredArgsConstructor
public class Sb1TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.name}")
    private String username;

    private final KafkaTemplate<String, VacancyUserDecision> kafkaTemplate;


    private void sendAnswerToTopic(Long vacancyId, UserDecision decision) throws TelegramApiException {
        VacancyUserDecision vacancyUserDecision = new VacancyUserDecision(vacancyId, decision);
        kafkaTemplate.send("topic-userAnswer", vacancyUserDecision)
                .whenComplete((result, ex) -> {
                    if (Objects.isNull(ex)) {
                        log.info("Vacancy with id{} sent to topic, {}", vacancyId, "topic-userAnswer");
                    } else {
                        log.error("Failed to send message for vacancy {}", vacancyId, ex);
                    }
                });
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Update received: {}", update);
        CallbackQuery callback = update.getCallbackQuery();
        try {
            String data = callback.getData();
            String callbackId = callback.getId();

            log.info("Received callback: {}", data);

            if (data.startsWith("approve:")) {
                Long vacancyId = Long.parseLong(data.split(":")[1]);
                sendAnswerToTopic(vacancyId, APPROVE);
                answerCallback(callbackId, "Вакансия отправляется 🚀");
            } else if (data.startsWith("reject:")) {
                Long vacancyId = Long.parseLong(data.split(":")[1]);
                sendAnswerToTopic(vacancyId, REJECT);
                answerCallback(callbackId, "Вакансия отклонена ❌");
            }
        } catch (Exception e) {
            log.error("Telegram update processing error", e);
        }
    }

    private void answerCallback(String callbackId, String text) throws TelegramApiException {
        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .text(text)
                .showAlert(false)
                .build();

        execute(answer);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
