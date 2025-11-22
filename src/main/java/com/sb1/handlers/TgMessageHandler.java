package com.sb1.handlers;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramBot;
import org.telegram.telegrambots.meta.api.objects.File;

import java.io.ByteArrayOutputStream;
import java.util.Optional;


public class TgMessageHandler {
    private final SilentSender sender;
    private final String botName;
    private final String botToken;

    public TgMessageHandler(SilentSender sender, String botName, String botToken) {
        this.sender = sender;
        this.botName = botName;
        this.botToken = botToken;
    }

    public void replyToStart(Long chatId) {
        sender.send("Hi!", chatId);
    }

//    public String getPointsOfCat(byte[] image) {
//
////        sender.send("Send a cat photo", chatId);
////        Optional<Message> optMsg = extractPhotoFromUpdate(update);
////
////        if (optMsg.isPresent()) {
////            Message msg = optMsg.get();
////            String fileId = msg.getPhoto().getFirst().getFileId();
////            InputFile inputFile = new InputFile(fileId);
////            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
////            GetFile getFile = new GetFile();
////            getFile.setFileId(fileId);
//////            File file = AbilityBot
//////            try {
//////                URLConnection connection = new URL("https://api.telegram.org/bot8457634646:AAHeTeK3VOpNdrzS3IhzBlyltj6edcVhj6w/getFile?file_id=AgACAgIAAxkBAAMXaQ38pD87_SOAzm_u3cPbMvP8ZZ0AAiUPaxtfdHBIzybeKXl5MTYBAAMCAANtAAM2BA").openConnection();
//////                InputStream is = connection.getInputStream();
//////            } catch (IIOException | MalformedURLException e) {
//////
//////            } catch (IOException e) {
//////                throw new RuntimeException(e);
//////            }
////
//////            URLConnection connection = new URL("https://api.telegram.org/file/bot<token>/<file_path>" + filePath).openConnection();
//////            InputStream is = connection.getInputStream();
//////
//////            // Прочитаем файл в массив байтов
//////            byte[] fileContent = Files.readAllBytes(is.readAllBytes());
//////            byte[] img =
////            sender.send("Size: ", chatId);
////        }
//    }

    private Optional<Message> extractPhotoFromUpdate(Update update) {
        return Optional.ofNullable(update.getMessage())
                .filter(Message::hasPhoto);
    }
}
