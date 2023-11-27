package com.petroandrushchak.telegram.my;

import com.petroandrushchak.telegram.my.model.UserRequest;
import com.petroandrushchak.telegram.my.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class CoinMisterBotNew extends TelegramLongPollingBot {

    private final Dispatcher dispatcher;
    private final UserSessionService userSessionService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String textFromUser = update.getMessage().getText();

            Long userId = update.getMessage().getChatId();
            String userFirstName = update.getMessage().getFrom().getFirstName();

            log.info("[{}, {}] : {}", userId, userFirstName, textFromUser);

            Long chatId = update.getMessage().getChatId();
            UserSession session = userSessionService.getSession(chatId);

            UserRequest userRequest = UserRequest.builder()
                                                 .update(update)
                                                 .userSession(session)
                                                 .chatId(chatId)
                                                 .build();

            boolean dispatched = dispatcher.dispatch(userRequest);

            if (!dispatched) {
                log.warn("Message is not dispatched, Unexpected update from user");
            }
        } else if (update.hasCallbackQuery()) {
            log.info("Callback query: {}", update.getCallbackQuery().getData());

            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            UserSession session = userSessionService.getSession(chatId);

            UserRequest userRequest = UserRequest.builder()
                                                 .update(update)
                                                 .userSession(session)
                                                 .chatId(chatId)
                                                 .build();
            boolean dispatched = dispatcher.dispatch(userRequest);

            if (!dispatched) {
                log.warn("Callback not dispatched, Unexpected update from user");
            }


        } else {
            log.warn("Unexpected update from user");
        }
    }

    public CoinMisterBotNew(Dispatcher dispatcher, UserSessionService userSessionService) {
        super("6530289671:AAHMDWkOSW8-nstjk7cLW0_hqClQTEbu2fc");
        this.dispatcher = dispatcher;
        this.userSessionService = userSessionService;
    }

    @Override
    public String getBotUsername() {
        return "CoinMisterPro";
    }

}
