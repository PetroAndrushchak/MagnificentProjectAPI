package com.petroandrushchak.futbin.steps;

import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.futbin.models.FutBinRawPlayer;
import com.petroandrushchak.futbin.models.search.page.FutBinPlayersSearchFilter;
import com.petroandrushchak.futbin.pages.PlayersPage;
import com.petroandrushchak.futbin.pages.components.PrivacyModal;
import com.petroandrushchak.helper.Waiter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import static org.awaitility.Awaitility.await;

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

    public List<FutBinRawPlayer> parseAllPlayers(FutBinPlayersSearchFilter searchFilter) {
        List<FutBinRawPlayer> allPlayers = new ArrayList<>();
        //playersPage.setSearchFiltersForPage(searchFilter, 1);
        playersPage.paginationButtonForPageNumberShouldBeSelected(1);

        var playersFromFirstPage = playersPage.parsePlayersDisplayedOnThePage();
        allPlayers.addAll(playersFromFirstPage);

        int totalNumberOfPages = playersPage.getTotalNumberOfPages();

        for (int i = 2; i <= totalNumberOfPages; i++) {
            playersPage.clickOnThePaginationButton(i)
                       .paginationButtonForPageNumberShouldBeSelected(i);
            var playersFromNextPage = playersPage.parsePlayersDisplayedOnThePage();
            allPlayers.addAll(playersFromNextPage);
        }

        log.info("Finished parsing all players");

        System.out.println("sdfsdfsdf");
        return allPlayers;
    }

    public List<FutBinRawPlayer> parseAllPlayersSemiManual() {
        List<FutBinRawPlayer> allPlayers = new ArrayList<>();
        playersPage.paginationButtonForPageNumberShouldBeSelected(1);

        Synthesizer synthesizer = createSynthesizer();

        var playersFromFirstPage = playersPage.parsePlayersDisplayedOnThePage();
        allPlayers.addAll(playersFromFirstPage);
        playSound(synthesizer);

        int totalNumberOfPages = playersPage.getTotalNumberOfPages();

        for (int i = 2; i <= totalNumberOfPages; i++) {
            waitUntilNextPageIsOpened(i);
            var playersFromNextPage = playersPage.parsePlayersDisplayedOnThePage();
            allPlayers.addAll(playersFromNextPage);
            playSound(synthesizer);
        }

        closeSynthesizer(synthesizer);
        return allPlayers;
    }

    private void waitUntilNextPageIsOpened(int pageNumber) {
        await().pollInSameThread()
               .atMost(Duration.ofMinutes(2))
               .pollInterval(Duration.ofSeconds(1))
               .until(() -> {
                   return playersPage.isPaginationButtonForPageNumberDisplayed(pageNumber);
               });

    }

    @SneakyThrows
    private Synthesizer createSynthesizer() {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");
        Central.registerEngineCentral("com.sun.speech.freetts" + ".jsapi.FreeTTSEngineCentral");
        Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
        synthesizer.allocate();
        synthesizer.resume();
        return synthesizer;
    }

    @SneakyThrows
    private void playSound(Synthesizer synthesizer) {
            synthesizer.speakPlainText("DONE", null);
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    }

    @SneakyThrows
    private void closeSynthesizer(Synthesizer synthesizer) {
        synthesizer.deallocate();
    }

}
