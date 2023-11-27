package com.petroandrushchak.telegram.old.test;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardCoinMisterProFactory {

    public static InlineKeyboardMarkup getStartMenu() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(InlineKeyboardButton.builder().text("DISCUSSION").callbackData("test1").build());
        rowInline.add(InlineKeyboardButton.builder().text("SMALL TALK").callbackData("test2").build());

        rowsInline.add(rowInline);
        inlineKeyboard.setKeyboard(rowsInline);
        return inlineKeyboard;
    }

}
