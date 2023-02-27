package com.petroandrushchak.steps;

import com.petroandrushchak.exceptions.BrowserProcessFutAccountBlocked;
import com.petroandrushchak.mapper.ui.api.PlayerItemMapper;
import com.petroandrushchak.service.BrowserProcessService;
import com.petroandrushchak.service.FutAccountService;
import com.petroandrushchak.view.request.SnippingRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SnippingValidationsSteps {

    @Autowired
    FutAccountService futAccountService;

    @Autowired
    BrowserProcessService browserProcessService;

    public void validateSnippingRequest(SnippingRequestBody snippingRequestBody) {

        log.info("Start Validation for Snipping Request: " + snippingRequestBody);

        // Step 1: Validate if Fut Account exists
        var futAccount = futAccountService.getFutAccountById(snippingRequestBody.getFutEaAccountId());
        log.info("Fut Account: " + futAccount);

        // Step 2: Validate if Fut Account is not used in any already running sessions
        var isBrowserProcessRunningForFutAccount = browserProcessService.isAnyBrowserProcessRunningForFutAccount(futAccount);
        if (isBrowserProcessRunningForFutAccount) {
            throw new BrowserProcessFutAccountBlocked(snippingRequestBody.getFutEaAccountId());
        }

        //Step 3: Validate fields which are preset are valida from Mongo DB
        //Player Name, Player Rating, Player id
        // nationality;
        // league;
        // club;

        //Step 3: Validate mapping for Fut Player Item
        var playerItemView = PlayerItemMapper.INSTANCE.playerItemRequestToView(snippingRequestBody.getPlayer());
        log.info("Player Item View: " + playerItemView);
    }


}
