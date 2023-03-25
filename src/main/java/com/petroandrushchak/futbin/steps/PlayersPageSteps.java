package com.petroandrushchak.futbin.steps;

import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.futbin.models.FutBinPlayer;
import com.petroandrushchak.futbin.models.FutBinPlayersSearchFilter;
import com.petroandrushchak.futbin.pages.PlayersPage;
import com.petroandrushchak.futbin.pages.components.PrivacyModal;
import com.petroandrushchak.helper.Waiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PlayersPageSteps {

    @Autowired PlayersPage playersPage;
    @Autowired PrivacyModal privacyModal;

    @RealPerson
    public PlayersPageSteps openPlayersPageInBrowser() {
      //  playersPage.open();
        return this;
    }

    @RealPerson
    public void closePrivacyModalIfOpened() {
        log.info("Waiting for privacy modal to be displayed");
        Waiter.isConditionTrueAtMostDuringPeriod(Duration.ofSeconds(10), privacyModal::isDisplayed);
        if (privacyModal.isDisplayed()) {
            log.info("Privacy modal is displayed");
            privacyModal.agree();
        } else {
            log.info("Privacy modal is not displayed");
        }
    }

    public void parseAllPlayers(FutBinPlayersSearchFilter searchFilter) {
        List<FutBinPlayer> allPlayers = new ArrayList<>();
        playersPage.setSearchFiltersForPage(searchFilter, 1);
        playersPage.paginationButtonForPageNumberShouldBeSelected(1);

       var playersFromFirstPage =  playersPage.parsePlayersDisplayedOnThePage();
        allPlayers.addAll(playersFromFirstPage);

        int totalNumberOfPages = playersPage.getTotalNumberOfPages();

        for (int i = 2; i < totalNumberOfPages; i++) {
            playersPage.clickOnThePaginationButton(i)
                       .paginationButtonForPageNumberShouldBeSelected(i);
            var playersFromNextPage =  playersPage.parsePlayersDisplayedOnThePage();
            allPlayers.addAll(playersFromNextPage);
        }

        log.info("Finished parsing all players");

        System.out.println("sdfsdfsdf");
    }

}
