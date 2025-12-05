package com.sb1.clients;

import com.sb1.enums.NNServices;
import com.sb1.interfaces.SendRequestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

import static org.telegram.abilitybots.api.objects.Flag.PHOTO;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;


@Component
public class CatBotAbility extends AbilityBot {

    @Autowired
    private SendRequestImpl sendRequestImpl;

    public CatBotAbility(@Value("${telegramm.bot.name}") String botName,
                         @Value("${telegramm.bot.token}") String botToken
    ) {
        super(botToken, botName);
    }

    @Override
    public long creatorId() {
        return 1L;
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info("Starts the bot")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("Hi!", ctx.chatId()))
                .build();
    }

    public Ability photoSize() {
        return Ability
                .builder()
                .name(DEFAULT)              // Не указываем команду
                .info("Показывает размер полученной фотографии.")
                .flag(PHOTO)
                .locality(USER)
                .privacy(PUBLIC)
                .input(0)
                .action(ctx -> {
                    Message msg = ctx.update().getMessage();
                    if (msg.hasPhoto()) {
                        try {
                            byte[] image = getByteArray(ctx.update());
                            silent.send("Оценка котику: " + sendRequestImpl.sendImageToTextRequest("Ты специалист по котикам", "Оцени кота по шкале от 1 до 100", image, NNServices.GIGA_CHAT), ctx.chatId());  //  Тут бросаем запрос в кафку
                        } catch (TelegramApiException e) {
                            silent.send("Ошибка при получении файла: " + e.getMessage(), ctx.chatId());
                        }
                    } else {
                        silent.send("Отправьте фотографию кота.", ctx.chatId());
                    }
                })
                .build();
    }

    private byte[] getByteArray(Update update) throws TelegramApiException {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        // Берем самую большую версию фотографии
        PhotoSize largest = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow();

        // Получаем информацию о файле
        GetFile getFile = new GetFile();
        getFile.setFileId(largest.getFileId());
        File file = execute(getFile);

        // Формируем URL для скачивания
        String fileUrl = file.getFileUrl(getBotToken());

        // Читаем байты
        try (InputStream in = new URL(fileUrl).openStream()) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
