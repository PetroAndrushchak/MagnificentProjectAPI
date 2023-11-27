package com.petroandrushchak.telegram.my.handler;

import com.petroandrushchak.telegram.my.model.UserRequest;
import com.petroandrushchak.telegram.my.helper.KeyboardHelper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
public class StartCommandHandler extends UserRequestHandler{

    private static String command = "/start";

    @Override
    public boolean isApplicable(UserRequest userRequest) {
        return isCommand(userRequest.getUpdate(), command);
    }

    @Override
    public void handle(UserRequest request) {
        ReplyKeyboard replyKeyboard = KeyboardHelper.buildMainMenu();
        telegramService.sendMessage(request.getChatId(),
                "Let's start!",
                replyKeyboard);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
