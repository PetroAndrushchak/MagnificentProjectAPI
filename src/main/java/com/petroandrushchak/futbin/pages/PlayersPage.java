package com.petroandrushchak.futbin.pages;

import com.codeborne.selenide.*;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.futbin.models.FutBinPlayer;
import com.petroandrushchak.futbin.models.FutBinPlayersSearchFilter;
import com.petroandrushchak.futbin.models.MinMaxPrice;
import com.petroandrushchak.futbin.models.Version;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    SelenideElement playerNameLink = $("a[class *='player_name_players']");
    SelenideElement playerClubLink = $("a[href *='&club']");
    SelenideElement playerNationLink = $("a[href *='&nation']");
    SelenideElement playerLeagueLink = $("a[href *='&league']");

    SelenideElement playerRatingLabel = $("td > .rating");
    SelenideElement playerPositionElement = $("td:has(span[class *='rating']) + td");

    SelenideElement playerPriceLabel = $("span:has(img[src *='coins'] )");

    public List<FutBinPlayer> parsePlayersDisplayedOnThePage() {
        return playerTableRows.asDynamicIterable().stream()
                              .map(this::parsePlayerFromRow)
                              .toList();
    }

    @RealPerson
    private FutBinPlayer parsePlayerFromRow(SelenideElement playerRow) {
        var futBinPlayer = new FutBinPlayer();

        String playerName = playerNameLink.getText();
        String playerId = getPlayerIdFromPlayerNameLink(playerNameLink.getAttribute("href"));

        String clubId = getPlayerClubIdFromPlayerClubLink(playerClubLink.getAttribute("href"));
        String clubName = playerClubLink.getAttribute("data-original-title");

        String nationId = getPlayerNationIdFromPlayerNationLink(playerNationLink.getAttribute("href"));
        String nationName = playerNationLink.getAttribute("data-original-title");

        String leagueId = getPlayerLeagueIdFromPlayerLeagueLink(playerLeagueLink.getAttribute("href"));
        String leagueName = playerLeagueLink.getAttribute("data-original-title");

        String playerRating = playerRatingLabel.getText();

        List<String> playerPositions = getPlayerAllPositions(playerPositionElement);

        String playerPrice = playerPriceLabel.getText();

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

        futBinPlayer.setPositions(playerPositions);
        futBinPlayer.setPriceText(playerPrice);

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

    private List<String> getPlayerAllPositions(SelenideElement selenideElement) {
        String allPositions =  selenideElement.$$("div").asDynamicIterable()
                       .stream().map(SelenideElement::getText)
                       .collect(Collectors.joining(","));
        return List.of(allPositions.split(","));
    }

    public static void main(String[] args) {

        String inputString = "/23/player/62/pele";
        String pattern = "/[0-9]+/player/([0-9]+)/[a-zA-Z]+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(inputString);
        if (m.find()) {
            String value = m.group(1);
            System.out.println(value); // prints "62"
        } else {
            System.out.println("Value not found");
        }
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

    @RealPerson
    public int getTotalNumberOfPages() {
        log.info("Waiting for pagination buttons to be displayed");
        currentlyVisiblePaginationButtons.shouldHave(sizeGreaterThan(0));
        var mapNumber = currentlyVisiblePaginationButtons.asFixedIterable()
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

    @RealPerson
    public void paginationButtonForPageNumberShouldBeSelected(int pageNumber) {
        var element = getElementForPageNumber(pageNumber);
        log.info("Waiting for pagination button with page number: {} to be displayed", pageNumber);
        element.parent().shouldHave(Condition.attribute("class", "page-item active"));
    }

    public SelenideElement getElementForPageNumber(int pageNumber) {
        var result = currentlyVisiblePaginationButtons.asFixedIterable()
                                                      .stream()
                                                      .filter(element -> element.getText()
                                                                                .equals(String.valueOf(pageNumber)))
                                                      .toList();
        if (result.size() != 1) {
            throw new RuntimeException("There is no element for page number: " + pageNumber);
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
