package com.petroandrushchak.fut.pages.fut;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.fut.exeptions.ItemCanNotBeFoundOnTheTransferMarket;
import com.petroandrushchak.fut.exeptions.NotFoundException;
import com.petroandrushchak.fut.pages.BasePage;
import com.petroandrushchak.helper.Waiter;
import com.petroandrushchak.fut.model.TransferMarketPrices;
import com.petroandrushchak.model.fut.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
@Component
public class FUTSearchTransferMarketPage extends BasePage<FUTSearchTransferMarketPage> {

    SelenideElement playerNameInputField = $(".ut-item-search-view .ut-player-search-control input");
    SelenideElement playerDropDownContainer = $(".inline-list");

    ElementsCollection playerDropDownItems = $$(".playerResultsList button");
    By playerDropDownItemNameLabel = By.cssSelector(".btn-text");
    By playerDropDownItemRatingLabel = By.cssSelector(".btn-subtext");

    SelenideElement maxBuyNowPriceInputField = $(".search-prices .price-filter:nth-of-type(6) .ut-number-input-control");
    SelenideElement minBuyNowPriceInputField = $(".search-prices .price-filter:nth-of-type(5) .ut-number-input-control");
    SelenideElement maxBidPriceInputField = $(".search-prices .price-filter:nth-of-type(3) .ut-number-input-control");
    SelenideElement minBidPriceInputField = $(".search-prices .price-filter:nth-of-type(2) .ut-number-input-control");

    SelenideElement searchButton = $(".call-to-action");

    public void setPlayerName(String playerName, int playerRating) {
        enterPlayerName(playerName);
        selectPlayerFromTheList(playerName, String.valueOf(playerRating));
    }

    @RealPerson
    private void enterPlayerName(String playerName) {
        playerNameInputField.click();
        playerNameInputField.clear();
        playerNameInputField.sendKeys(playerName);
    }

    @RealPerson
    private void selectPlayerFromTheList(String playerName, String playerRating) {
        log.info("Selecting player: " + playerName + " ,with rating: " + playerRating + " from the dropdown list");

        playerDropDownContainer.shouldBe(visible);

        log.info("Found --- " + playerDropDownItems.size() + " --- players in the dropdown list");

        var matchedPlayersLabels = playerDropDownItems.asDynamicIterable().stream()
                                                      .filter(playerLabel -> {
                                                          String name = playerLabel.find(playerDropDownItemNameLabel)
                                                                                   .getText();
                                                          String rating = playerLabel.find(playerDropDownItemRatingLabel)
                                                                                     .getText();

                                                          log.info("Found player with name: " + name + " , rating: " + rating);
                                                          if (name.equals(playerName) && rating.equals(String.valueOf(playerRating))) {
                                                              log.info("Found right player !!!");
                                                              return true;
                                                          } else {
                                                              return false;
                                                          }
                                                      }).toList();

        if (matchedPlayersLabels.size() != 1) {
            throw new ItemCanNotBeFoundOnTheTransferMarket();
        }
        matchedPlayersLabels.get(0).click();

        playerDropDownContainer.shouldBe(Condition.not(visible));
    }


    //Quality

    SelenideElement qualityDropDownContainer = $(".ut-item-search-view div[class *='ut-search-filter-control']:has(.ut-search-filter-control--row img[src *='images/SearchFilters/level'])");

    @RealPerson
    public void selectQuality(Quality quality) {
        if (!isQualityDropdownOpened()) {
            qualityDropDownContainer.click();
        }
        selectQualityFromDropDown(quality);
    }

    @RealPerson
    private boolean isQualityDropdownOpened() {
        return qualityDropDownContainer.getAttribute("class").contains("is-open");
    }

    @RealPerson
    private void selectQualityFromDropDown(Quality quality) {
        var qualityOptionStyleText = switch (quality) {
            case BRONZE -> "bronze.png";
            case SILVER -> "silver.png";
            case GOLD -> "gold.png";
            case SPECIAL -> "SP.png";
        };

        qualityDropDownContainer.find("ul li[style *='" + qualityOptionStyleText + "']").click();

        qualityDropDownContainer.find("img")
                                .shouldHave(Condition.attributeMatching("src", ".*" + qualityOptionStyleText));
    }

    //Rarity

    SelenideElement rarityDropDownContainer = $x("//div[contains(@class, 'ut-item-search-view')]//div[contains(@class, 'inline-list-select')] [.//img[contains(@src, 'images/SearchFilters/rarity') or contains(@src, 'itemBGs')]]");

    public void selectRarity(Rarity rarity) {
        var textOnUI = switch (rarity) {
            case COMMON -> "Common";
            case RARE -> "Rare";
            case CONMEBOL_LIBERTADORES -> "CONMEBOL LIBERTADORES";
            case CONMEBOL_SUDAMERICANA -> "CONMEBOL SUDAMERICANA";
            case DOMESTIC_MAN_OF_THE_MATCH -> "Domestic Man of the Match";
            case FANTASY_FUT -> "FANTASY FUT";
            case FANTASY_FUT_HEROES -> "FANTASY FUT HEROES";
            case FIFA_WORLD_CUP_PATH_TO_GLORY -> "FIFA WC Path To Glory";
            case FUT_BIRTHDAY -> "FUT Birthday";
            case FUT_BIRTHDAY_ICON -> "FUT BIRTHDAY ICON";
            case FUT_CENTURIONS -> "FUT CENTURIONS";
            case FUT_CHAMPIONS_TOTS -> "FUT Champions TOTS";
            case FUT_FUTURE_STARS -> "FUT Future Stars";
            case FUT_HEROES -> "FUT Heroes";
            case ICON -> "Icon";
            case ONE_TO_WATCH -> "Ones to Watch";
            case OUT_OF_POSITION -> "Out of Position";
            case RULE_BREAKERS -> "RULEBREAKERS";
            case TEAM_OF_THE_SEASON -> "Team of the Season";
            case TEAM_OF_THE_SEASON_MOMENTS -> "TEAM OF THE SEASON MOMENTS";
            case TEAM_OF_THE_WEEK -> "Team of the Week";
            case TEAM_OF_THE_YEAR -> "Team of the Year";
            case TOTY_HONOURABLE_MENTIONS -> "TOTY HONOURABLE MENTIONS";
            case TOTY_ICON -> "TOTY ICON";
            case TROPHY_TITANS -> "Trophy Titans Hero";
            case TROPHY_TITANS_ICON -> "Trophy Titans ICON";
            case UCL_ROAD_TO_THE_FINAL -> "UCL ROAD TO THE FINAL";
            case UCL_ROAD_TO_THE_KNOCKOUTS -> "UCL Road to the Knockouts";
            case UECL_ROAD_TO_THE_FINAL -> "UECL ROAD TO THE FINAL";
            case UECL_ROAD_TO_THE_KNOCKOUTS -> "UECL Road to the Knockouts";
            case UEL_ROAD_TO_THE_FINAL -> "UEL Road to the Final";
            case UEL_ROAD_TO_THE_KNOCKOUTS -> "UEL Road to the Knockouts";
            case WINTER_WILDCARDS -> "WINTER WILDCARDS";
            case WORLD_CUP_HERO -> "World Cup Hero";
            case WORLD_CUP_ICON -> "World Cup Icon";
            case WORLD_CUP_TEAM_OF_THE_TOURNAMENT -> "World Cup Team of the Tournament";
            default -> throw new IllegalStateException("Unexpected value: " + rarity);
        };

        if (!isRarityDropdownOpened()) {
            rarityDropDownContainer.click();
        }

        selectRarityFromDropDown(textOnUI);

    }

    @RealPerson
    private boolean isRarityDropdownOpened() {
        return rarityDropDownContainer.getAttribute("class").contains("is-open");
    }

    @RealPerson
    private void selectRarityFromDropDown(String textOnUI) {
        var foundRarityElements = rarityDropDownContainer.findAll("li[style *='/itemBGs/']")
                                                         .asDynamicIterable()
                                                         .stream()
                                                         .filter(element -> element.getText().equals(textOnUI))
                                                         .toList();
        if (foundRarityElements.size() != 1) {
            throw new NotFoundException("Rarity: " + textOnUI + " was not found in the dropdown list");
        }

        var element = foundRarityElements.get(0);

        element.click();
        rarityDropDownContainer.find("span").shouldHave(Condition.text(textOnUI));
    }

    // Position

    SelenideElement positionDropDownContainer = $(".ut-item-search-view div[class *='ut-search-filter-control']:has(.ut-search-filter-control--row img[src *='/positions/'])");

    @RealPerson
    public void selectPosition(Position position) {
        var textOnUi = switch (position) {
            case DEFENDERS -> "Defenders";
            case MIDFIELDERS -> "Midfielders";
            case ATTACKERS -> "Attackers";
            case GK -> "GK";
            case RWB -> "RWB";
            case RB -> "RB";
            case CB -> "CB";
            case LB -> "LB";
            case LWB -> "LWB";
            case CDM -> "CDM";
            case RM -> "RM";
            case CM -> "CM";
            case LM -> "LM";
            case CAM -> "CAM";
            case CF -> "CF";
            case RW -> "RW";
            case ST -> "ST";
            case LW -> "LW";
            default -> throw new IllegalStateException("Unexpected value: " + position);
        };

        if (!isPositionDropdownOpened()) {
            positionDropDownContainer.click();
        }

        selectPositionFromDropDown(textOnUi);
    }

    @RealPerson
    private boolean isPositionDropdownOpened() {
        return positionDropDownContainer.getAttribute("class").contains("is-open");
    }

    @RealPerson
    private void selectPositionFromDropDown(String textOnUi) {
        var foundPositionElements = positionDropDownContainer.findAll("li[style *='/positions/']")
                                                             .asDynamicIterable()
                                                             .stream()
                                                             .filter(element -> element.getText().equals(textOnUi))
                                                             .toList();
        if (foundPositionElements.size() != 1) {
            throw new NotFoundException("Position: " + textOnUi + " was not found in the dropdown list");
        }

        var element = foundPositionElements.get(0);

        element.click();
        positionDropDownContainer.find("span").shouldHave(Condition.text(textOnUi));
    }

    //Chemistry Style

    SelenideElement chemistryStyleDropDownContainer = $(".ut-item-search-view div[class *='ut-search-filter-control']:has(.ut-search-filter-control--row img[src *='/chemistrystyles/'])");

    public void selectChemistryStyle(ChemistryStyle chemistryStyle) {
        var textOnUi = switch (chemistryStyle) {
            case BASIC -> "BASIC";
            case SNIPER -> "SNIPER";
            case FINISHER -> "FINISHER";
            case DEADEYE -> "DEADEYE";
            case MARKSMAN -> "MARKSMAN";
            case HAWK -> "HAWK";
            case ARTIST -> "ARTIST";
            case ARCHITECT -> "ARCHITECT";
            case POWERHOUSE -> "POWERHOUSE";
            case MAESTRO -> "MAESTRO";
            case ENGINE -> "ENGINE";
            case SENTINEL -> "SENTINEL";
            case GUARDIAN -> "GUARDIAN";
            case GLADIATOR -> "GLADIATOR";
            case BACKBONE -> "BACKBONE";
            case ANCHOR -> "ANCHOR";
            case HUNTER -> "HUNTER";
            case CATALYST -> "CATALYST";
            case SHADOW -> "SHADOW";
            case WALL -> "WALL";
            case SHIELD -> "SHIELD";
            case CAT -> "CAT";
            case GLOVE -> "GLOVE";
            case GK_BASIC -> "GK BASIC";
        };

        if (!isChemistryStyleDropdownOpened()) {
            chemistryStyleDropDownContainer.click();
        }

        selectChemistryStyleFromDropDown(textOnUi);
    }

    @RealPerson
    private boolean isChemistryStyleDropdownOpened() {
        return chemistryStyleDropDownContainer.getAttribute("class").contains("is-open");
    }

    @RealPerson
    private void selectChemistryStyleFromDropDown(String textOnUi) {
        var foundChemistryStyleElements = chemistryStyleDropDownContainer.findAll("li[style *='/chemistrystyles/']")
                                                                         .asDynamicIterable()
                                                                         .stream()
                                                                         .filter(element -> element.getText()
                                                                                                   .equals(textOnUi))
                                                                         .toList();
        if (foundChemistryStyleElements.size() != 1) {
            throw new NotFoundException("Chemistry Style: " + textOnUi + " was not found in the dropdown list");
        }

        var element = foundChemistryStyleElements.get(0);

        element.click();
        chemistryStyleDropDownContainer.find("span").shouldHave(Condition.text(textOnUi));
    }

    //League

    SelenideElement leagueDropDownContainer = $(".ut-item-search-view div[class *='ut-search-filter-control']:has(.ut-search-filter-control--row img[src *='leagueLogos'])");

    @RealPerson
    public void selectLeague(League league) {
        var leagueId = String.valueOf(league.getLeagueId());
        var leagueFullName = league.getLeagueFullName();
        var leagueShortAbbreviation = league.getLeagueShortAbbreviation();
        String leagueInDropdown = leagueFullName + " (" + leagueShortAbbreviation + ")";

        if (!isLeagueDropdownOpened()) {
            leagueDropDownContainer.click();
        }

        selectLeagueFromDropDown(leagueId, leagueInDropdown);
    }

    @RealPerson
    private boolean isLeagueDropdownOpened() {
        return leagueDropDownContainer.getAttribute("class").contains("is-open");
    }

    @RealPerson
    private void selectLeagueFromDropDown(String leagueId, String leagueInDropdown) {
        var foundLeagueElements = leagueDropDownContainer.findAll("li[style *='/leagueLogos/']")
                                                         .asDynamicIterable()
                                                         .stream()
                                                         .filter(element -> element.getText().equals(leagueInDropdown))
                                                         .toList();
        if (foundLeagueElements.size() != 1) {
            throw new NotFoundException("League: " + leagueInDropdown + " was not found in the dropdown list");
        }

        var element = foundLeagueElements.get(0);

        if (!element.getAttribute("style").contains(leagueId + ".png")) {
            throw new NotFoundException("Found league: " + leagueInDropdown + " ,but it has wrong id" + element.getAttribute("style"));
        }
        element.click();
        leagueDropDownContainer.find("span").shouldHave(Condition.text(leagueInDropdown));
    }

    // Club

    SelenideElement clubDropDownContainer = $(".ut-item-search-view div[class *='ut-search-filter-control']:has(.ut-search-filter-control--row img[src *='/clubs/'])");

    @RealPerson
    public void selectClub(Club club) {
        var clubId = String.valueOf(club.getClubId());
        var clubFullName = club.getClubLongAbbreviation();

        if (!isClubDropdownOpened()) {
            clubDropDownContainer.click();
        }

        selectClubFromDropDown(clubId, clubFullName);
    }

    @RealPerson
    private boolean isClubDropdownOpened() {
        return clubDropDownContainer.getAttribute("class").contains("is-open");
    }

    @RealPerson
    private void selectClubFromDropDown(String clubId, String clubFullName) {
        var foundClubElements = clubDropDownContainer.findAll("li[style *='/clubs/']")
                                                     .asDynamicIterable()
                                                     .stream()
                                                     .filter(element -> element.getText().equals(clubFullName))
                                                     .toList();
        if (foundClubElements.size() != 1) {
            throw new NotFoundException("Club: " + clubFullName + " was not found in the dropdown list");
        }

        var element = foundClubElements.get(0);

        if (!element.getAttribute("style").contains(clubId + ".png")) {
            throw new NotFoundException("Found club: " + clubFullName + " ,but it has wrong id" + element.getAttribute("style"));
        }
        element.click();
        clubDropDownContainer.find("span").shouldHave(Condition.text(clubFullName));
    }

    // Nation

    SelenideElement nationDropDownContainer = $(".ut-item-search-view div[class *='ut-search-filter-control']:has(.ut-search-filter-control--row img[src *='/flags/'])");

    public void selectNation(Nation nation) {
        var nationId = String.valueOf(nation.getNationId());
        var nationFullName = nation.getNationName();

        if (!isNationDropdownOpened()) {
            nationDropDownContainer.click();
        }

        selectNationFromDropDown(nationId, nationFullName);
    }

    private boolean isNationDropdownOpened() {
        return nationDropDownContainer.getAttribute("class").contains("is-open");
    }

    private void selectNationFromDropDown(String nationId, String nationInDropdown) {
        var foundNationElements = new java.util.ArrayList<>(nationDropDownContainer.findAll("li[style *='/flags/']")
                                                                                   .asDynamicIterable()
                                                                                   .stream()
                                                                                   .filter(element -> element.getText()
                                                                                                             .equals(nationInDropdown))
                                                                                   .toList());
        if (foundNationElements.size() == 2) {
            var isTheSameCountry = foundNationElements.get(0).getAttribute("style").contains(nationId + ".png") &&
                    foundNationElements.get(1).getAttribute("style").contains(nationId + ".png");
            if (isTheSameCountry) {
                foundNationElements.remove(1);
            }
        }

        if (foundNationElements.size() != 1) {
            throw new NotFoundException("Nation: " + nationInDropdown + " is not found in the dropdown list, actual size: " + foundNationElements.size());
        }

        var element = foundNationElements.get(0);

        if (!element.getAttribute("style").contains(nationId + ".png")) {
            throw new NotFoundException("Found nation: " + nationInDropdown + " ,but it has wrong id" + element.getAttribute("style"));
        }
        element.click();
        nationDropDownContainer.find("span").shouldHave(Condition.text(nationInDropdown));
    }


    public void setSearchPrices(TransferMarketPrices prices) {
        if (prices.getMaxBuyNowPrice() == 0L) {
            clearMaxBuyNowPrice();
        } else {
            setMaxBuyNowPrice(prices.getMaxBuyNowPrice());
        }
        setMaxBidPrice(prices.getMaxBidPrice());

        setMinBuyNowPrice(prices.getMinBuyNowPrice());
        setMinBidPrice(prices.getMinBidPrice());

    }

    public void clearMaxBuyNowPrice() {
        maxBuyNowPriceInputField.click();
        Waiter.waitForOneSecond();
        maxBuyNowPriceInputField.clear();
    }

    @RealPerson
    public void setMaxBuyNowPrice(long price) {
        for (int i = 0; i < 5; i++) {
            maxBuyNowPriceInputField.click();
            maxBuyNowPriceInputField.clear();
            maxBuyNowPriceInputField.sendKeys(String.valueOf(price));
            minBidPriceInputField.click();
            Waiter.waitForOneSecond();
            if (isMaxBuyNowPriceSetCorrect(price)) {
                return;
            }
        }
        throw new RuntimeException("Max buy now price can not be set");
    }

    private boolean isMaxBuyNowPriceSetCorrect(long targetBuyNowPrice) {
        log.info("Checking if max buy now price is set correctly");
        String currentValue = getMaxBuyNowPrice();

        if (currentValue.isEmpty()) {
            log.info("Max buy now price is empty");
            return false;
        } else {
            int currentBuyNowMaxPrice = Integer.parseInt(getMaxBuyNowPrice().replace(",", ""));
            var result = currentBuyNowMaxPrice == targetBuyNowPrice;
            log.info("Max buy now price is set correctly: " + result);
            return result;
        }
    }

    private String getMaxBuyNowPrice() {
        maxBuyNowPriceInputField.click();
        return maxBuyNowPriceInputField.getValue();
    }

    @RealPerson
    public void setMinBuyNowPrice(long price) {
        minBuyNowPriceInputField.click();
        minBuyNowPriceInputField.clear();
        minBuyNowPriceInputField.sendKeys(String.valueOf(price));
    }

    @RealPerson
    public void setMaxBidPrice(long price) {
        maxBidPriceInputField.click();
        maxBidPriceInputField.clear();
        maxBidPriceInputField.sendKeys(String.valueOf(price));
    }

    @RealPerson
    public void setMinBidPrice(long price) {
        minBidPriceInputField.click();
        minBidPriceInputField.clear();
        minBidPriceInputField.sendKeys(String.valueOf(price));
    }

    public void clickSearchButton() {
        searchButton.click();
    }


    @Override
    public boolean isOpened() {
        return $(".ut-market-search-filters-view").is(visible);
    }

}
