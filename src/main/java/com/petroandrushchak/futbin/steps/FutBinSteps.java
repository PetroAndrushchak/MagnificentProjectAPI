package com.petroandrushchak.futbin.steps;

import com.petroandrushchak.futbin.models.FutBinPlayersSearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FutBinSteps {

    @Autowired PlayersPageSteps playersPageSteps;

    public void parseAllPlayers(FutBinPlayersSearchFilter searchFilter) {
//        playersPageSteps.openPlayersPageInBrowser()
//                        .closePrivacyModalIfOpened();

        playersPageSteps.parseAllPlayers(searchFilter);
    }
}
