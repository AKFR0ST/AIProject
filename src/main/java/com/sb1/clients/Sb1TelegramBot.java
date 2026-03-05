package com.sb1.clients;

import com.sb1.services.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class Sb1TelegramBot extends TelegramLongPollingBot {

    private final TelegramService telegramService;

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${telegram.bot.username}")
    private String username;

    public Sb1TelegramBot(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            telegramService.handleUpdate(update);
        } catch (Exception e) {
            log.error("Telegram update processing error", e);
        }
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
