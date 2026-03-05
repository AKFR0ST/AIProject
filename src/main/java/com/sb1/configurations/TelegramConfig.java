package com.sb1.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

//@Configuration
public class TelegramConfig {

//    @Value("${telegramm.bot.token}")
//    private String token;
//
//    @Value("${telegramm.bot.name}")
//    private String name;
//
//    @Bean
//    public TelegramLongPollingBot telegramBot() {
//        return new TelegramLongPollingBot() {
//
//            @Override
//            public void onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update update) {
//
//                // Здесь будет вызов TelegramService
//            }
//
//            @Override
//            public String getBotUsername() {
//                return name;
//            }
//
//            @Override
//            public String getBotToken() {
//                return token;
//            }
//        };
//    }
}
