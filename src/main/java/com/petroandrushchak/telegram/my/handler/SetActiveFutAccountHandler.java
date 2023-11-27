package com.petroandrushchak.telegram.my.handler;

import com.petroandrushchak.telegram.my.constant.ButtonActionMessage;
import com.petroandrushchak.telegram.my.db.FUT_Accounts;
import com.petroandrushchak.telegram.my.model.UserRequest;
import org.springframework.stereotype.Component;

@Component
public class SetActiveFutAccountHandler extends UserRequestHandler {

    @Override
    public boolean isApplicable(UserRequest request) {
        return request.getUpdate().hasCallbackQuery() && request.getUpdate()
                                                                .getCallbackQuery()
                                                                .getMessage()
                                                                .getText()
                                                                .equals(ButtonActionMessage.SELECT_ACTIVE_FUT_ACCOUNT);
    }

    @Override
    public void handle(UserRequest dispatchRequest) {
        var futAccountToSet = dispatchRequest.getUpdate().getCallbackQuery().getData();
        if (FUT_Accounts.isFutAccountInTheList(futAccountToSet)) {
            FUT_Accounts.setActiveFutAccount(futAccountToSet);
            telegramService.sendMessage(dispatchRequest.getChatId(), "Active FUT Account is set to: " + futAccountToSet);
        }else {
            telegramService.sendMessage(dispatchRequest.getChatId(), "FUT Account is not in the db");
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
