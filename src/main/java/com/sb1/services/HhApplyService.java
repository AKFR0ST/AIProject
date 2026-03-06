package com.sb1.services;

import com.microsoft.playwright.*;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class HhApplyService {

    private BrowserContext context;
    private final Browser browser;

    public HhApplyService() {
        Playwright playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        initializeContext();
    }

    private void initializeContext() {
        File storageFile = new File("hh-session.json");
        if (storageFile.exists()) {
            // используем сохранённую сессию
            context = browser.newContext(
                    new Browser.NewContextOptions().setStorageStatePath(storageFile.toPath())
            );
        } else {
            // авторизация вручную
            context = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://hh.ru");
            System.out.println("Пожалуйста, авторизуйтесь вручную в браузере...");
            // После логина:
            context.storageState(new BrowserContext.StorageStateOptions().setPath(storageFile.toPath()));
        }
    }

    public void apply(Long vacancyId) {
        Page page = context.newPage();
        page.navigate("https://hh.ru/vacancy/" + vacancyId);

        // нажимаем кнопку "Откликнуться"
        Locator button = page.locator("button:has-text('Отклик')");
        if (button.count() > 0) {
            button.first().click();

            // иногда появляется textarea для сопроводительного письма
            if (page.locator("textarea").count() > 0) {
                page.locator("textarea").fill("Здравствуйте! Заинтересовала ваша вакансия.");
            }

            page.locator("button:has-text('Отправить')").click();
        }
    }
}
