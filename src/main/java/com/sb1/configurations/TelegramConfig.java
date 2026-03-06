package com.sb1.configurations;

import com.sb1.clients.Sb1TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(Sb1TelegramBot bot) throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
        return botsApi;
    }

//    @Value("${telegram.bot.token}")
//    private String token;
//
//    @Value("${telegram.bot.name}")
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
