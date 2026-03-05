package com.sb1.services;

import com.sb1.clients.Sb1TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class TelegramSender {

    private final Sb1TelegramBot bot;

    public void send(SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void answer(AnswerCallbackQuery query) {
        try {
            bot.execute(query);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
