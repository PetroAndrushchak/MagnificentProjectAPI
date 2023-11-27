package com.petroandrushchak.telegram.my.handler;

import com.petroandrushchak.telegram.my.TelegramService;
import com.petroandrushchak.telegram.my.model.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class UserRequestHandler {

    @Autowired
    protected TelegramService telegramService;

    public abstract boolean isApplicable(UserRequest request);
    public abstract void handle(UserRequest dispatchRequest);
    public abstract boolean isGlobal();

    public boolean isCommand(Update update, String command) {
        return update.hasMessage() && update.getMessage().isCommand()
                && update.getMessage().getText().equals(command);
    }

    public boolean isCallbackQuery(Update update) {
        return update.hasCallbackQuery();
    }

    public boolean isTextMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    public boolean isTextMessage(Update update, String text) {
        return update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(text);
    }
}
