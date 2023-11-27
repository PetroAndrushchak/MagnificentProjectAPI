package com.petroandrushchak.telegram.my.handler;

import com.petroandrushchak.service.telegram.ActiveFutAccountService;
import com.petroandrushchak.telegram.my.TelegramService;
import com.petroandrushchak.telegram.my.db.FUT_Accounts;
import com.petroandrushchak.telegram.my.model.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.petroandrushchak.telegram.my.constant.ButtonActionMessage.WHAT_IS_ACTIVE_FUT_ACCOUNT;

@Component
public class DisplayActiveFutAccountHandler extends UserRequestHandler {

    @Autowired ActiveFutAccountService activeFutAccountService;

    @Override
    public void handle(UserRequest dispatchRequest) {
        String message;
//        if (activeFutAccountService.isActiveFutAccountSet()) {
//            message = "Active FUT Account is: " + activeFutAccountService.getActiveFutAccountName();
//        } else {
//            message = "No FUT Account is not set";
//        }
    //    telegramService.sendMessage(dispatchRequest.getChatId(), message);
    }

    @Override
    public boolean isApplicable(UserRequest request) {
        return isTextMessage(request.getUpdate()) && request.getUpdate().getMessage().getText().equals(WHAT_IS_ACTIVE_FUT_ACCOUNT);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
