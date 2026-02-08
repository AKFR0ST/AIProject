package com.sb1.services;

import com.sb1.clients.LocalClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocalAPIService {
    @Autowired
    LocalClient localClient;

    public String textToTextRequest(String text){
        return localClient.localTextToTextRequest(text);
    }
}
