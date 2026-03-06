package com.sb1.services;

import com.sb1.clients.Sb1TelegramBot;
import com.sb1.dto.VacancyUserDecision;
import com.sb1.models.hh.Vacancy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    public static final String VACANCY_SENT_TO_USER_FOR_APPROVE = "Vacancy {} sent to user for approve";
    private final Sb1TelegramBot bot;

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