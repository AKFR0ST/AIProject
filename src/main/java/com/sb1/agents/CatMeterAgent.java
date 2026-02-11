package com.sb1.agents;

import com.sb1.clients.CatBotAbility;
import com.sb1.interfaces.SendRequestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CatMeterAgent implements Runnable{  // Переименовать в агенты?

    @Autowired
    SendRequestImpl sendRequest;

    @Autowired
    CatBotAbility catBotAbility;
    Integer offset;

    @Override
    public void run() {

        while(true){
//            String incomingRequest = tgClient.getUp
            //  Вычитываем из входящего топика сообщения

            //  Передаем сообщения в импл ГЧата

            //  Ответ передаем в исходящий топик

        }
    }
}
