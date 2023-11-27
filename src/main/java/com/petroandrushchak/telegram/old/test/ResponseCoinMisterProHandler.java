package com.petroandrushchak.telegram.old.test;

import com.petroandrushchak.telegram.old.test.KeyboardCoinMisterProFactory;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class ResponseCoinMisterProHandler {

    private final SilentSender sender;

    public ResponseCoinMisterProHandler(SilentSender sender, DBContext db) {
        this.sender = sender;
    }

    public void replyToStart(long chatId) {
        var keyboard = KeyboardCoinMisterProFactory.getStartMenu();
        sender.execute(SendMessage.builder().replyMarkup(keyboard).chatId(chatId).text("Hello!").build());
    }

    public boolean userIsActive(Long chatId) {
        return true;
    }
}
