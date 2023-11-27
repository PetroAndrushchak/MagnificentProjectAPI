package com.petroandrushchak.telegram.old.test;

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


public class TestBot extends AbilityBot {

    private final ResponseHandler responseHandler;

    protected TestBot() {
        super("6530289671:AAHMDWkOSW8-nstjk7cLW0_hqClQTEbu2fc", "CoinMisterPro");
        responseHandler = new ResponseHandler(silent, db);
    }

    public Ability sayHello() {
        return Ability
                .builder()
                .name("hello")
                .info("says hello")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("Hello!", ctx.chatId()))
                .build();
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

    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> responseHandler.replyToButtons(getChatId(upd), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsActive(getChatId(upd)));
    }

    @Override
    public long creatorId() {
        return 1L;
    }
}
