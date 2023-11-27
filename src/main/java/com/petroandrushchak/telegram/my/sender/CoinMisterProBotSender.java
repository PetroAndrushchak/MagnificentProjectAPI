package com.petroandrushchak.telegram.my.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Slf4j
@Component
public class CoinMisterProBotSender extends DefaultAbsSender {

    protected CoinMisterProBotSender() {
        super(new DefaultBotOptions());
    }

    @Override
    public String getBotToken() {
        return "6530289671:AAHMDWkOSW8-nstjk7cLW0_hqClQTEbu2fc";
    }
}