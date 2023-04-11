package com.petroandrushchak.futbin.steps;

import com.petroandrushchak.futbin.models.FutBinRawPlayer;
import com.petroandrushchak.futbin.models.FutBinPlayersSearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FutBinSteps {

    @Autowired PlayersPageSteps playersPageSteps;

    public List<FutBinRawPlayer> parseAllPlayers(FutBinPlayersSearchFilter searchFilter) {
//        playersPageSteps.openPlayersPageInBrowser()
//                        .closePrivacyModalIfOpened();

       return playersPageSteps.parseAllPlayersSemiManual();
    }
}
