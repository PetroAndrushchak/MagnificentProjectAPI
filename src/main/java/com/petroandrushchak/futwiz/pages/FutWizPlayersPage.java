package com.petroandrushchak.futwiz.pages;

import com.petroandrushchak.futwiz.models.FutWizRawPlayer;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class FutWizPlayersPage {

    public boolean isPageContainsPlayers(String pageHtml) {
        Document jsoup = Jsoup.parse(pageHtml);
        return jsoup.select(".player-search-list .player-search-result-row ").size() > 0;
    }

    public List<FutWizRawPlayer> parsePlayerFromPage(String playerPageHtml) {
        Document jsoup = Jsoup.parse(playerPageHtml);

        var playerCards = jsoup.select(".player-search-list .player-search-result-row ");
        log.info("Found {} player cards", playerCards.size());
        return playerCards.stream()
                          .map(this::parseListRowPlayer)
                          .toList();
    }

    public FutWizRawPlayer parseListRowPlayer(Element playerRow) {
        log.debug("Parsing player from row");
        var futWizPlayer = new FutWizRawPlayer();

        var playerUrl = playerRow.select(".face a").attr("href");
        var internalPlayerId = playerUrl.substring(playerUrl.lastIndexOf("/") + 1);
        futWizPlayer.setInternalId(internalPlayerId);

        var imageSrc = playerRow.select(".face img").attr("src");
        if (isPlayerHasLastDigitInLink(imageSrc)) {
            var playerFutId = getPlayerLastDigitFromPlayerLink(imageSrc);
            futWizPlayer.setFutId(playerFutId);
        }

        var playerName = playerRow.select(".player-name b").text();
        futWizPlayer.setName(playerName);

        var clubSrc = playerRow.select(".player-name-data .player-club img").attr("src");
        var clubId = getPlayerLastDigitFromPlayerLink(clubSrc);
        futWizPlayer.setClubId(clubId);

        var clubName = playerRow.select(".player-nation-data .player-league-info a:nth-of-type(1)").text();
        futWizPlayer.setClubName(clubName);

        var nationIdSrc = playerRow.select(".player-nation-data .player-club img").attr("src");
        var nationId = getPlayerLastDigitFromPlayerLink(nationIdSrc);
        futWizPlayer.setNationId(nationId);

        var leagueIdSrc = playerRow.select(".player-nation-data .player-league-info a:nth-of-type(2)").attr("href");
        var leagueId = getPlayerLeagueId(leagueIdSrc);
        futWizPlayer.setLeagueId(leagueId);

        var leagueNameSrc = playerRow.select(".player-nation-data .player-league-info a:nth-of-type(2)").text();
        futWizPlayer.setLeagueName(leagueNameSrc);

        var rating = playerRow.select(".version a div div").text();
        futWizPlayer.setRating(rating);

        var qualityAndRarity = playerRow.select(".version a > div").attr("class");
        futWizPlayer.setQualityAndRarity(Arrays.asList(getPlayerQualityAndRarity(qualityAndRarity).split("-")));

        var mainPosition = playerRow.select(".positions .mainpos").text();
        futWizPlayer.setMainPosition(mainPosition);

        var otherPositions = playerRow.select(".positions .secondary-pos").text();

        if (!otherPositions.isEmpty()) {
            futWizPlayer.setOtherPositions(Arrays.asList(otherPositions.split(" ")));
        }

        var priceText = playerRow.select(".price").text();
        futWizPlayer.setPriceText(priceText);

        return futWizPlayer;
    }

    private boolean isPlayerHasLastDigitInLink(String playerImageLink) {
        try {
            getPlayerLastDigitFromPlayerLink(playerImageLink);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getPlayerLastDigitFromPlayerLink(String playerImageLink) {
        Pattern pattern = Pattern.compile("(\\d+)\\.");
        Matcher matcher = pattern.matcher(playerImageLink);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new RuntimeException("Can't find player id in link " + playerImageLink);
        }
    }

    private String getPlayerQualityAndRarity(String classAttribute) {
        return classAttribute.substring(classAttribute.lastIndexOf("otherversion24-") + "otherversion24-".length());
    }

    private String getPlayerLeagueId(String classAttribute) {
        return classAttribute.substring(classAttribute.lastIndexOf("=") + 1);
    }

}
