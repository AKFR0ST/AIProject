package com.sb1.utils;

public final class TelegramHtmlFormatter {

    private TelegramHtmlFormatter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String format(String html) {

        if (html == null) {
            return "";
        }

        return html
                .replace("<strong>", "<b>")
                .replace("</strong>", "</b>")
                .replace("<em>", "<i>")
                .replace("</em>", "</i>")
                .replace("<ul>", "")
                .replace("</ul>", "\n")
                .replace("<li>", "• ")
                .replace("</li>", "\n")
                .replace("<p>", "\n")
                .replace("</p>", "\n")
                .replaceAll("<[^>]*>", "");
    }
}
