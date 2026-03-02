package com.sb1;

import com.sb1.clients.GigaChatClient;
import com.sb1.clients.CatBotAbility;
import com.sb1.clients.LocalClient;
import com.sb1.services.ResumeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@EnableScheduling
@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class Sb1Application {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = SpringApplication.run(Sb1Application.class, args);

//        GigaChatClient gigaChatClient = context.getBean(GigaChatClient.class);
//        String res = gigaChatClient.gigaChatTextToTextRequest("Двоечник", "Сколько будет 2+2");
//        System.out.println(res);

        Path path = Path.of("C:\\Users\\FROST\\Desktop\\java-jun-KovalevAM\\force\\KovalevAM(Java Middle).docx");
        MultipartFile multipartFile = new MockMultipartFile(
                "file",                              // имя параметра
                "KovalevAM(Java Middle).docx",             // имя файла
                Files.probeContentType(path),        // MIME тип
                Files.readAllBytes(path)             // содержимое
        );

        ResumeService resumeService = context.getBean(ResumeService.class);
        resumeService.addNewResume(multipartFile);


//        multipartFile.getResource().getFile("C:\\Users\\FROST\\Desktop\\java-jun-KovalevAM\\force\\KovalevAM(Java Middle).docx")
    }}
//        SendRequestImpl sendRequestImpl = context.getBean(SendRequestImpl.class);
//        GigaChatClient gigaChatClient = context.getBean(GigaChatClient.class);
//        LocalClient localClient = context.getBean(LocalClient.class);

//        String resp = localClient.localTextToTextRequest("Отвечай как двоечник", "Сколько будет два плюс два?");
//        System.out.println(resp);


//        try {
//            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            botsApi.registerBot(context.getBean(CatBotAbility.class));
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//        gigaChatClient.printToken();
//        byte[] byteArray = Files.readAllBytes(Paths.get("C:/122.bmp"));
//        System.out.println("Размер(byte): " + byteArray.length);
////        System.out.println(gigaChatClient.loadImage(byteArray).toString());
//        System.out.println(gigaChatClient.gigaChatImageToTextRequest("Художник", "Что изображено на картинке", byteArray));
////        System.out.print(sendRequestImpl.sendTextToTextRequest("Ответь как специалист по котам", "Сколько мышей может поймать кот за декаду. Ответ должен содержать только одно число. В ответе не должно быть текста или любых пояснений.", NNServices.GIGA_CHAT));
////                System.out.print(gigaChatClient.gigaChatTextToTextRequest("Ответь как специалист по котам", "Сколько мышей может поймать кот за декаду. Ответ должен содержать только одно число. В ответе не должно быть текста или любых пояснений."));

//    }
//
//}
