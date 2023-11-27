package com.petroandrushchak.futbin.pages;

import com.codeborne.selenide.*;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.aop.RetryStep;
import com.petroandrushchak.futbin.models.FutBinRawPlayer;
import com.petroandrushchak.futbin.models.search.page.FutBinPlayersSearchFilter;
import com.petroandrushchak.futbin.models.search.page.MinMaxPrice;
import com.petroandrushchak.futbin.models.search.page.Version;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
@Component
public class PlayersPage {

    private static final String PLAYERS_PAGE_URL = "https://www.futbin.com/players";

    ElementsCollection currentlyVisiblePaginationButtons = $$("li[class *='page-item' ] a:not(a[aria-label='Next'])");

    @Autowired
    SearchFilterComponent searchFilterComponent;

    ElementsCollection playerTableRows = $$("tr[class *='player_tr']");

    By playerNameLinkBy = By.cssSelector("a[class *='player_name_players']");
    By playerClubLinkBy = By.cssSelector("a[href *='&club']");
    By playerNationLinkBy = By.cssSelector("a[href *='&nation']");
    By playerLeagueLinkBy = By.cssSelector("a[href *='&league']");

    By playerRatingLabelBy = By.cssSelector("span.rating");
    By playerPositionElementBy = By.cssSelector("td:has(span[class *='rating']) + td");

    By playerPriceLabelBy = By.cssSelector("span:has(img[src *='coins'] )");

    public List<FutBinRawPlayer> parsePlayersDisplayedOnThePage() {
        log.info("Parsing players displayed on the page");
        var result = playerTableRows.asDynamicIterable().stream()
                                    .map(this::parsePlayerFromRow)
                                    .toList();

        log.info("Found {} players", result.size());
        return result;
    }

    private FutBinRawPlayer parsePlayerFromRow(SelenideElement playerRow) {
        log.info("Parsing player from row");
        var futBinPlayer = new FutBinRawPlayer();
        var playerRawElementHtml = playerRow.getAttribute("innerHTML");
        Document jsoup = Jsoup.parse(playerRawElementHtml);

        var playerNameLink = jsoup.select("a[class *='player_name_players']").get(0);
        String playerName = playerNameLink.text();
        String playerId = getPlayerIdFromPlayerNameLink(playerNameLink.attr("href"));

        var playerClubLink = jsoup.select("a[href *='&club']").get(0);
        String clubId = getPlayerClubIdFromPlayerClubLink(playerClubLink.attr("href"));
        String clubName = playerClubLink.attr("data-original-title");

        var playerNationLink = jsoup.select("a[href *='&nation']").get(0);
        String nationId = getPlayerNationIdFromPlayerNationLink(playerNationLink.attr("href"));
        String nationName = playerNationLink.attr("data-original-title");

        var playerLeagueLink = jsoup.select("a[href *='&league']").get(0);
        String leagueId = getPlayerLeagueIdFromPlayerLeagueLink(playerLeagueLink.attr("href"));
        String leagueName = playerLeagueLink.attr("data-original-title");

        String playerRating = jsoup.select("span.rating").text();

        String playerQualityAndRarityClass = jsoup.select("span.rating").get(0).attr("class");
        String qualityAndRarity = getPlayerQualityAndRarity(playerQualityAndRarityClass);

        var firstPositionElement = jsoup.select(".font-weight-bold").get(0);
        String firstPosition = firstPositionElement.text();
        String additionalPositions = "";
        var nextPositionSibling = firstPositionElement.nextElementSibling();
        if (nextPositionSibling != null) {
            additionalPositions = nextPositionSibling.text();
        }

        List<String> playerPositions = getPlayerAllPositions(firstPosition, additionalPositions);

        String playerPrice = jsoup.select("span:has(img[src *='coins'] )").get(0).text();

        // Setting Value
        futBinPlayer.setId(playerId);
        futBinPlayer.setName(playerName);

        futBinPlayer.setClubId(clubId);
        futBinPlayer.setClubName(clubName);

        futBinPlayer.setNationId(nationId);
        futBinPlayer.setNationName(nationName);

        futBinPlayer.setLeagueId(leagueId);
        futBinPlayer.setLeagueName(leagueName);

        futBinPlayer.setRating(playerRating);
        futBinPlayer.setQualityAndRarity(qualityAndRarity);

        futBinPlayer.setPositions(playerPositions);
        futBinPlayer.setPriceText(playerPrice);

        log.info("Finished parsing player from row");
        return futBinPlayer;

    }

    private String getPlayerIdFromPlayerNameLink(String path) {
        String pattern = "/[0-9]+/player/([0-9]+)/[a-zA-Z]+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(path);
        if (m.find()) {
            return m.group(1);
        } else {
            throw new RuntimeException("Player id not found in player name link");
        }
    }

    private String getPlayerClubIdFromPlayerClubLink(String path) {
        String pattern = ".*&club=([0-9]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(path);
        if (m.find()) {
            return m.group(1);
        } else {
            throw new RuntimeException("Player club id not found in player club link");
        }
    }

    private String getPlayerNationIdFromPlayerNationLink(String path) {
        String pattern = ".*&nation=([0-9]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(path);
        if (m.find()) {
            return m.group(1);
        } else {
            throw new RuntimeException("Player nation id not found in player nation link");
        }
    }

    private String getPlayerLeagueIdFromPlayerLeagueLink(String path) {
        String pattern = ".*&league=([0-9]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(path);
        if (m.find()) {
            return m.group(1);
        } else {
            throw new RuntimeException("Player league id not found in player league link");
        }
    }

    private List<String> getPlayerAllPositions(String firstPosition, String additionalPositions) {
        if (additionalPositions.isBlank()) {
            return List.of(firstPosition);
        }
        String allPositions = firstPosition + "," + additionalPositions;
        return List.of(allPositions.split(","));
    }

    private List<String> getPlayerAllPositions(SelenideElement selenideElement) {
        String allPositions = selenideElement.$$("div").asDynamicIterable()
                                             .stream().map(SelenideElement::getText)
                                             .collect(Collectors.joining(","));
        return List.of(allPositions.split(","));
    }

    private String getPlayerQualityAndRarity(String classAttribute) {
        return classAttribute.substring(classAttribute.indexOf("ut23") + 4);
    }

    @RealPerson
    public void setSearchFiltersForPage(FutBinPlayersSearchFilter searchFilter, int pageNumber) {
        if (searchFilter.isVersionPresent()) {
            searchFilterComponent.selectVersion(searchFilter.getVersion());
        }

        if (searchFilter.isMinMaxPricePresent()) {
            searchFilterComponent.setPriceRange(searchFilter.getMinMaxPrice()
                                                            .getMinPrice(), searchFilter.getMinMaxPrice()
                                                                                        .getMaxPrice());
        }
    }

    public void open() {
        WebDriverRunner.getWebDriver().get(PLAYERS_PAGE_URL);
    }

    public int getTotalNumberOfPages() {
        log.info("Waiting for pagination buttons to be displayed");
        currentlyVisiblePaginationButtons.shouldHave(sizeGreaterThan(0));
        var mapNumber = currentlyVisiblePaginationButtons.asDynamicIterable()
                                                         .stream().map(element -> element.getText())
                                                         .map(Integer::parseInt)
                                                         .max(Integer::compareTo)
                                                         .get();
        log.info("Total number of pages: {}", mapNumber);
        return mapNumber;
    }

    @RealPerson
    public PlayersPage clickOnThePaginationButton(int pageNumber) {
        var element = getElementForPageNumber(pageNumber);
        log.info("Clicking on the pagination button with page number: {}", pageNumber);
        element.click();
        return this;
    }

    // @RealPerson
    public void paginationButtonForPageNumberShouldBeSelected(int pageNumber) {
        var element = getElementForPageNumber(pageNumber);
        log.info("Waiting for pagination button with page number: {} to be displayed", pageNumber);
        element.parent().shouldHave(Condition.attribute("class", "page-item active"), Duration.ofSeconds(30));
    }

    public boolean isPaginationButtonForPageNumberDisplayed(int pageNumber) {
        var element = getElementForPageNumber(pageNumber);
        log.info("Waiting for pagination button with page number: {} to be displayed", pageNumber);
        return element.parent().is(Condition.attribute("class", "page-item active"));
    }


    @RetryStep
    public SelenideElement getElementForPageNumber(int pageNumber) {
        currentlyVisiblePaginationButtons.shouldHave(sizeGreaterThan(1));

        var result = currentlyVisiblePaginationButtons.asDynamicIterable()
                                                      .stream()
                                                      .filter(element -> element.getText()
                                                                                .equals(String.valueOf(pageNumber)))
                                                      .toList();
        if (result.size() != 1) {
            throw new RuntimeException("There is no element for page number: " + pageNumber + " size: " + result.size());
        } else {
            return result.get(0);
        }
    }


    // Deprecated
    public String createPageUrlForSearchFilter(FutBinPlayersSearchFilter searchFilter, int pageNumber) {
        var queryParamsMap = new HashMap<String, String>();

        queryParamsMap.put("page", createPageQueryParam(pageNumber));

        if (searchFilter.isVersionPresent()) {
            queryParamsMap.put(QueryParamKey.VERSION.getKey(), createVersionQueryParam(searchFilter.getVersion()));
        }

        if (searchFilter.isMinMaxPricePresent()) {
            queryParamsMap.put(QueryParamKey.PRICE_RANGE.getKey(), createPriceRangeQueryParams(searchFilter.getMinMaxPrice()));
        }

        return PLAYERS_PAGE_URL + queryParamsUrlPart(queryParamsMap);

    }

    public static String queryParamsUrlPart(Map<String, String> queryParams) {
        return queryParams.entrySet().stream()
                          .map(entry -> entry.getKey() + "=" + entry.getValue())
                          .reduce((a, b) -> a + "&" + b)
                          .map(s -> "?" + s)
                          .orElse("");
    }

    private String createPriceRangeQueryParams(MinMaxPrice minMaxPrice) {
        return minMaxPrice.getMinPrice() + "-" + minMaxPrice.getMaxPrice();
    }

    private String createVersionQueryParam(Version version) {
        return switch (version) {
            case ALL_GOLD -> "gold";
            case GOLD_RARE -> "gold_rare";
            case GOLD_NON_RARE -> "gold_nr";
            case ALL_GOLD_PLUS_SPECIAL -> "gold_all";
        };
    }

    private String createPageQueryParam(int pageNumber) {
        return String.valueOf(pageNumber);
    }

    public void waitUntilNextPageIsOpened(int i) {
        log.info("Waiting for pagination button with page number: {} to be displayed", i);
        paginationButtonForPageNumberShouldBeSelected(i);
    }


    private enum QueryParamKey {

        PRICE_RANGE("ps_price"),
        VERSION("version");

        String key;

        QueryParamKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }


}
