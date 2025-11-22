package com.sb1.enums;

import lombok.Getter;

@Getter
public enum GigaChatModels {
    GIGA_CHAT_2("GigaChat-2"),
    GIGACHAT_2_MAX("GigaChat-2-Max"),
    GIGACHAT_2_PRO("GigaChat-2-Pro");

    private String title;

    private GigaChatModels(String title) {
        this.title = title;
    }
}
