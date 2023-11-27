package com.petroandrushchak.telegram.my.handler;

import com.petroandrushchak.service.firebase.FutAccountServiceFirebase;
import com.petroandrushchak.telegram.my.constant.ButtonActionMessage;
import com.petroandrushchak.telegram.my.model.UserRequest;
import com.petroandrushchak.view.FutEaAccountView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class SelectActiveFutAccountHandler extends UserRequestHandler {

    @Autowired FutAccountServiceFirebase futAccountService;

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate()) && request.getUpdate().getMessage().getText().equals(ButtonActionMessage.SELECT_ACTIVE_FUT_ACCOUNT);
    }

    @Override
    public void handle(UserRequest dispatchRequest) {

        var futAccountNames = futAccountService.findAllFutAccounts().stream()
                                               .map(FutEaAccountView::getUsername)
                                               .toList();

        var buttonLists = futAccountNames.stream()
                                         .map(futAccountName -> InlineKeyboardButton.builder()
                                                                                    .text(futAccountName)
                                                                                    .callbackData(futAccountName)
                                                                                    .build())
                                         .toList();

        var keyboardM1 = InlineKeyboardMarkup.builder()
                                             .keyboardRow(buttonLists).build();

        telegramService.sendMessage(dispatchRequest.getChatId(), ButtonActionMessage.SELECT_ACTIVE_FUT_ACCOUNT, keyboardM1);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
