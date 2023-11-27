package com.petroandrushchak.telegram.my.helper;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static com.petroandrushchak.telegram.my.constant.ButtonActionMessage.SELECT_ACTIVE_FUT_ACCOUNT;
import static com.petroandrushchak.telegram.my.constant.ButtonActionMessage.WHAT_IS_ACTIVE_FUT_ACCOUNT;

public class KeyboardHelper {

    public static ReplyKeyboardMarkup buildMainMenu() {
        KeyboardRow keyboardRow = new KeyboardRow();

        keyboardRow.add(WHAT_IS_ACTIVE_FUT_ACCOUNT);
        keyboardRow.add(SELECT_ACTIVE_FUT_ACCOUNT);

        return ReplyKeyboardMarkup.builder()
                                  .keyboard(List.of(keyboardRow))
                                  .selective(true)
                                  .resizeKeyboard(true)
                                  .oneTimeKeyboard(false)
                                  .build();
    }

}
