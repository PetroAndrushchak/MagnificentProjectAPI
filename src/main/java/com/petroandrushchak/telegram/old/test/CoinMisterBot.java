package com.petroandrushchak.telegram.old.test;

import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Component
public class CoinMisterBot extends AbilityBot {

    private final ResponseCoinMisterProHandler responseHandler;

    protected CoinMisterBot() {
        super("6530289671:AAHMDWkOSW8-nstjk7cLW0_hqClQTEbu2fc", "CoinMisterPro");
        responseHandler = new ResponseCoinMisterProHandler(silent, db);
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public Reply reply() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> {
//            responseHandler.replyToButtons(getChatId(upd), upd.getMessage());
            responseHandler.replyToStart(getChatId(upd));
        };
        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsActive(getChatId(upd)));
    }


    @Override
    public long creatorId() {
        return 1L;
    }
}
