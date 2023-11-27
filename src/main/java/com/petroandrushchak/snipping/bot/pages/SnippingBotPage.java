package com.petroandrushchak.snipping.bot.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.fut.exeptions.ItemCanNotBeFoundOnTheTransferMarket;
import com.petroandrushchak.model.fut.PlayerItem;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Slf4j
@Component
public class SnippingBotPage {

    SelenideElement typePlayerNameInputField = $(".exclusion-settings *[class *='ut-player-search-control'] input");

    SelenideElement playerDropDownContainer = $(".search-prices .playerResultsList");
    ElementsCollection playerDropDownItems = $$(".search-prices .playerResultsList button");

    By playerDropDownItemNameLabel = By.cssSelector(".btn-text");
    By playerDropDownItemRatingLabel = By.cssSelector(".btn-subtext");


    SelenideElement excludePlayerButton = $(".exclusion-settings .ut-player-search-control + button");

    ElementsCollection excludedPlayersList = $$(".table-column div[type = 'player']");

    public void excludePlayer(PlayerItem playerItem) {
        if (playerItem.isPlayerNamePresent()) {
            if (playerItem.isPlayerNickNamePresent()) {
                setPlayerName(playerItem.getNickName(), playerItem.getRating());
            } else {
                setPlayerName(playerItem.getPlayerName(), playerItem.getRating());
            }
        }

        excludePlayerButton.click();

        playerWithIdShouldBeExclude(String.valueOf(playerItem.getId()));
    }

    public void setPlayerName(String playerName, int playerRating) {
        enterPlayerName(playerName);
        selectPlayerFromTheList(playerName, String.valueOf(playerRating));
    }

    @RealPerson
    private void enterPlayerName(String playerName) {
        typePlayerNameInputField.click();
        typePlayerNameInputField.clear();
        typePlayerNameInputField.sendKeys(playerName);
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

        log.info("Player selected successfully");
    }

    // Excluded Players List

    public List<String> getCurrentlyExcludedPlayersIds() {
        return excludedPlayersList.asFixedIterable().stream()
                                  .map(player -> player.getAttribute("value"))
                                  .toList();
    }

    public void playerWithIdShouldBeExclude(String id) {
        Selenide.$(".table-column div[type = 'player'][value = '" + id + "']").shouldBe(visible);
    }

    public void playerWithIdShouldNotBeExclude(String id) {
        Selenide.$(".table-column div[type = 'player'][value = '" + id + "']").shouldNotBe(visible);
    }

    public void deletePlayerFromExclusionById(String id) {
        Selenide.$(".table-column div[type = 'player'][value = '" + id + "'] .sb-remove-exclusion").click();
    }
}
