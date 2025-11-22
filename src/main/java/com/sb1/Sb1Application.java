package com.sb1;

import com.sb1.clients.GigaChatClient;
import com.sb1.clients.CatBotAbility;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class Sb1Application {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = SpringApplication.run(Sb1Application.class, args);
//        SendRequestImpl sendRequestImpl = context.getBean(SendRequestImpl.class);
        GigaChatClient gigaChatClient = context.getBean(GigaChatClient.class);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(context.getBean(CatBotAbility.class));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
//        gigaChatClient.printToken();
//        byte[] byteArray = Files.readAllBytes(Paths.get("C:/122.bmp"));
//        System.out.println("Размер(byte): " + byteArray.length);
////        System.out.println(gigaChatClient.loadImage(byteArray).toString());
//        System.out.println(gigaChatClient.gigaChatImageToTextRequest("Художник", "Что изображено на картинке", byteArray));
////        System.out.print(sendRequestImpl.sendTextToTextRequest("Ответь как специалист по котам", "Сколько мышей может поймать кот за декаду. Ответ должен содержать только одно число. В ответе не должно быть текста или любых пояснений.", NNServices.GIGA_CHAT));
////                System.out.print(gigaChatClient.gigaChatTextToTextRequest("Ответь как специалист по котам", "Сколько мышей может поймать кот за декаду. Ответ должен содержать только одно число. В ответе не должно быть текста или любых пояснений."));

    }

}
