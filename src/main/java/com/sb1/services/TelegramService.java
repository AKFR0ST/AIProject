package com.sb1.services;

import com.sb1.clients.Sb1TelegramBot;
import com.sb1.dto.VacancyUserDecision;
import com.sb1.enums.UserDecision;
import com.sb1.models.hh.Vacancy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;

import static com.sb1.enums.UserDecision.APPROVE;
import static com.sb1.enums.UserDecision.REJECT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    public static final String VACANCY_SENT_TO_USER_FOR_APPROVE = "Vacancy {} sent to user for approve";
    private final Sb1TelegramBot bot;
    private final KafkaTemplate<String, VacancyUserDecision> kafkaTemplate;

    // Отправка вакансии
    public void sendVacancy(Long chatId, Vacancy vacancy) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(buildText(vacancy))
                .replyMarkup(buildKeyboard(vacancy))
                .build();

        bot.execute(message);
        log.info(VACANCY_SENT_TO_USER_FOR_APPROVE, vacancy.getId());
    }

    // Обработка апдейта
    public void handleUpdate(Update update) throws TelegramApiException {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    // Обработка кнопок approve/reject
    private void handleCallback(CallbackQuery callback) throws TelegramApiException {
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
    }

    private void sendAnswerToTopic(Long vacancyId, UserDecision decision) throws TelegramApiException {
        VacancyUserDecision vacancyUserDecision = new VacancyUserDecision(vacancyId, decision);
        kafkaTemplate.send("topic-userAnswer", vacancyUserDecision)
                .whenComplete((result, ex) -> {
                    if (Objects.isNull(ex)) {
                        log.info("Vacancy with id{} sent to topic", vacancyId);
                    } else {
                        log.error("Failed to send message for vacancy {}", vacancyId, ex);
                    }
                });
    }

    private void answerCallback(String callbackId, String text) throws TelegramApiException {
        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .text(text)
                .showAlert(false)
                .build();

        bot.execute(answer);
    }

    private InlineKeyboardMarkup buildKeyboard(Vacancy vacancy) {
        InlineKeyboardButton approve = InlineKeyboardButton.builder()
                .text("✅ Отправить")
                .callbackData("approve:" + vacancy.getId())
                .build();

        InlineKeyboardButton reject = InlineKeyboardButton.builder()
                .text("❌ Отклонить")
                .callbackData("reject:" + vacancy.getId())
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(approve, reject))
                .build();
    }

    private String buildText(Vacancy vacancy) {
        return """
                🏢 %s
                📍 %s
                💰 %s
                ---
                %s
                ---
                %s
                """.formatted(
                vacancy.getName(),
                vacancy.getEmployer(),
                vacancy.getSalaryFrom() != null
                        ? vacancy.getSalaryFrom() + " - " + vacancy.getSalaryTo()
                        : "Не указана",
                vacancy.getDescription(),
                vacancy.getCoverLetter()
        );
    }
}