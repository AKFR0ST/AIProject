package com.sb1.runnables;

import com.sb1.clients.CatBotAbility;
import com.sb1.interfaces.SendRequestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CatMeterRunnable implements Runnable{

    @Autowired
    SendRequestImpl sendRequest;

    @Autowired
    CatBotAbility catBotAbility;
    Integer offset;

    @Override
    public void run() {

        while(true){
//            String incomingRequest = tgClient.getUp

        }
    }
}
