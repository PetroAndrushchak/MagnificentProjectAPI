package com.petroandrushchak.snipping.bot.actions;

import com.petroandrushchak.futbin.models.FutBinNewRawPlayer;
import com.petroandrushchak.futwiz.steps.FutWizParsePlayersSteps;
import com.petroandrushchak.model.fut.PlayerItem;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.service.FutBinService;
import com.petroandrushchak.snipping.bot.pages.SnippingBotPage;
import com.petroandrushchak.steps.FutBinMappingSteps;
import com.petroandrushchak.steps.FutPlayersMapperSteps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.petroandrushchak.service.LocalBrowserHelper.connectToAlreadyOpenedBrowser;

@Slf4j
@SpringBootTest
public class PopulatePlayersToExclude {

    @Autowired FutBinService futBinService;
    @Autowired SnippingBotPage snippingBotPage;
    @Autowired FutPlayersMapperSteps futPlayersMapperSteps;
    @Autowired FutBinMappingSteps futBinMappingSteps;
    @Autowired FutWizParsePlayersSteps futWizParsePlayersSteps;


    @Test
    void fillInExcludedPlayers() {

        //TODO Find player which is in two teams and if price for any is lower exclude both
        //FUT_WIZ source
        //    var allPlayer = futWizParsePlayersSteps.parsePlayersFromFutWiz("https://www.futwiz.com/en/fc24/players?page=0&release[]=commongold");


        //FUT_BIN source
        var playersFileName = "all_non_rare_players_data.json";
        List<FutBinNewRawPlayer> futBinRawPlayers = futBinService.parsePlayersFromJsonFile(playersFileName);

        List<ThirdPartySitePlayer> allPlayer = futBinMappingSteps.mapNewRawPlayersToPlayers(futBinRawPlayers);

        var minPriceToBuy = 500;
        var maxPriceToBuy = 49000;

        //Filter players by price, it should be 600 or more
        var rawPlayersToExclude = getPlayersToExcludeMoreThanPriceBuyLessThan(0, 1150, allPlayer);
        var rawPlayersNOTtoExclude = getPlayersToNOTExclude(minPriceToBuy, allPlayer);

        var playersToExclude = futPlayersMapperSteps.mapThirdPartyPlayers(rawPlayersToExclude);
        var playersToNotExclude = futPlayersMapperSteps.mapThirdPartyPlayers(rawPlayersNOTtoExclude);

        log.info("Players to exclude: " + playersToExclude.size());

        connectToAlreadyOpenedBrowser();

        var playersFailedToExclude = new ArrayList<PlayerItem>();

        List<String> currentlyExcludedPlayersIds = snippingBotPage.getCurrentlyExcludedPlayersIds();

        List<PlayerItem> newPlayersToExclude = getPlayersToExclude(playersToExclude, currentlyExcludedPlayersIds);
        List<PlayerItem> playersToDeleteFromExcluded = getPlayersToRemoveFromExclusion(playersToNotExclude, currentlyExcludedPlayersIds);

        log.info("Players to exclude: " + newPlayersToExclude.size());
        playersToDeleteFromExcluded.forEach(
                playerToDeleteFromExclusion -> {
                    var playerId = String.valueOf(playerToDeleteFromExclusion.getId());
                    log.info("Deleting player from exclusion: " + playerToDeleteFromExclusion);
                    if (isPlayerIssueWithExcluding(playerId)) {
                        log.info("Player with id: " + playerToDeleteFromExclusion + " has issue with excluding, skipping");
                        return;
                    }
                    try {
                        snippingBotPage.deletePlayerFromExclusionById(playerId);
                        snippingBotPage.playerWithIdShouldNotBeExclude(playerId);
                    } catch (Exception | Error e) {
                        log.error("Failed to delete player from exclusion: " + playerToDeleteFromExclusion);
                        e.printStackTrace();
                    }
                }
        );

        log.info("Players to exclude: " + newPlayersToExclude.size());
        newPlayersToExclude.forEach(
                playerToExclude -> {
                    log.info("Excluding player: " + playerToExclude.getPlayerName() + " with rating: " + playerToExclude.getRating());
                    if (isPlayerIssueWithExcluding(String.valueOf(playerToExclude.getId()))) {
                        log.info("Player with id: " + playerToExclude.getId() + " has issue with excluding, skipping");
                        return;
                    }

                    try {
                        snippingBotPage.excludePlayer(playerToExclude);
                    } catch (Exception | Error e) {
                        log.error("Failed to exclude player: " + playerToExclude.getPlayerName() + " with rating: " + playerToExclude.getRating());
                        playersFailedToExclude.add(playerToExclude);
                        e.printStackTrace();
                    }
                }
        );

        log.info("Players failed to exclude: " + playersFailedToExclude.size());
        playersFailedToExclude.stream()
                              .forEach(player -> log.info("Player failed to exclude: " + player.getId() + " with rating: " + player.getRating()));

        System.out.println("sdfsdf");
    }

    private List<PlayerItem> getPlayersToExclude(List<PlayerItem> playersToExclude, List<String> currentlyExcludedPlayersId) {
        return playersToExclude.stream()
                               .filter(Objects::nonNull)
                               .filter(player -> !currentlyExcludedPlayersId.contains(String.valueOf(player.getId())))
                               .toList();
    }

    private List<PlayerItem> getPlayersToRemoveFromExclusion(List<PlayerItem> playersNotToExclude, List<String> currentlyExcludedPlayersId) {

        return playersNotToExclude.stream()
                                  .filter(Objects::nonNull)
                                  .filter(player -> {
                                      var stringId = String.valueOf(player.getId());
                                      return currentlyExcludedPlayersId.contains(stringId);
                                  }).collect(Collectors.toList());

    }

    public boolean isPlayerIssueWithExcluding(String id) {
        List<String> playersIdsNotShowingInSearch = new ArrayList<>();
        playersIdsNotShowingInSearch.add("205288"); //Fernando
        playersIdsNotShowingInSearch.add("234779");
        playersIdsNotShowingInSearch.add("236295");
        playersIdsNotShowingInSearch.add("228716");
        playersIdsNotShowingInSearch.add("259681");
        playersIdsNotShowingInSearch.add("206098");
        playersIdsNotShowingInSearch.add("216283");

        //https://www.futbin.com/24/player/19722/martha-thomas

        return playersIdsNotShowingInSearch.contains(id);
    }

    private List<ThirdPartySitePlayer> getPlayersToExclude(long minBuyPrice, List<ThirdPartySitePlayer> allPlayers) {

        List<ThirdPartySitePlayer> playersToExclude = new ArrayList<>();

        var allNotDuplicatedPlayers = allPlayers.stream()
                                                .filter(player -> allPlayers.stream()
                                                                            .filter(player1 -> Objects.equals(player1.getFutId(), player.getFutId()))
                                                                            .count() == 1)
                                                .toList();

        var notDuplicatedPlayersToExclude = allNotDuplicatedPlayers.stream()
                                                                   .filter(playerPriceLessThanMinBuyPrice(minBuyPrice))
                                                                   .toList();
        playersToExclude.addAll(notDuplicatedPlayersToExclude);


        var duplicatedPlayers = allPlayers.stream()
                                          .filter(player -> allPlayers.stream()
                                                                      .filter(player1 -> Objects.equals(player1.getFutId(), player.getFutId()))
                                                                      .count() > 1)
                                          .toList();

        var uniquePlayersFutId = duplicatedPlayers.stream()
                                                  .map(ThirdPartySitePlayer::getFutId)
                                                  .collect(Collectors.toSet());

        var duplicatedPlayersToExclude = uniquePlayersFutId.stream().map(playerFutId -> {

                                                               var duplicatePlayersPair = duplicatedPlayers.stream().filter(player -> Objects.equals(player.getFutId(), playerFutId)).toList();

                                                               var arePlayersPairToExclude = duplicatePlayersPair.stream()
                                                                                                                 .anyMatch(playerPriceLessThanMinBuyPrice(minBuyPrice));

                                                               if (arePlayersPairToExclude) {
                                                                   return duplicatePlayersPair.stream().findAny().get();
                                                               } else {
                                                                   return null;
                                                               }
                                                           }).filter(Objects::nonNull)
                                                           .toList();

        playersToExclude.addAll(duplicatedPlayersToExclude);


        return playersToExclude;

    }

    private List<ThirdPartySitePlayer> getPlayersToExcludeMoreThanPriceBuyLessThan(long minBuyPrice, long maxBuyPrice, List<ThirdPartySitePlayer> allPlayers) {

        List<ThirdPartySitePlayer> playersToExclude = new ArrayList<>();

        var allNotDuplicatedPlayers = allPlayers.stream()
                                                .filter(player -> allPlayers.stream()
                                                                            .filter(player1 -> Objects.equals(player1.getFutId(), player.getFutId()) ||
                                                                                    (player.getPlayerName().equals(player1.getPlayerName())
                                                                                            && player.getClubId().equals(player1.getClubId())
                                                                                            && player.getLeagueId().equals(player1.getLeagueId())
                                                                                            && player.getNationId().equals(player1.getNationId())))
                                                                            .count() == 1)
                                                .toList();

        var notDuplicatedPlayersToExclude = allNotDuplicatedPlayers.stream()
                                                                   .filter(player -> player.getPrice() >= minBuyPrice && player.getPrice() < maxBuyPrice)
                                                                   .toList();
        playersToExclude.addAll(notDuplicatedPlayersToExclude);


        var duplicatedPlayers = allPlayers.stream()
                                          .filter(player -> allPlayers.stream()
                                                                      .filter(player1 -> Objects.equals(player1.getFutId(), player.getFutId()) ||
                                                                              (player.getPlayerName().equals(player1.getPlayerName())
                                                                                      && player.getClubId().equals(player1.getClubId())
                                                                                      && player.getLeagueId().equals(player1.getLeagueId())
                                                                                      && player.getNationId().equals(player1.getNationId())))
                                                                      .count() > 1)
                                          .toList();

        var uniquePlayersFutId = duplicatedPlayers.stream()
                                                  .map(ThirdPartySitePlayer::getFutId)
                                                  .collect(Collectors.toSet());

        var duplicatedPlayersToExclude = uniquePlayersFutId.stream().map(playerFutId -> {

                                                               var duplicatePlayersPair = duplicatedPlayers.stream().filter(player -> Objects.equals(player.getFutId(), playerFutId)).toList();

                                                               var arePlayersPairToExclude = duplicatePlayersPair.stream()
                                                                                                                 .anyMatch(player -> player.getPrice() >= minBuyPrice && player.getPrice() < maxBuyPrice);

                                                               if (arePlayersPairToExclude) {
                                                                   return duplicatePlayersPair.stream().findAny().get();
                                                               } else {
                                                                   return null;
                                                               }
                                                           }).filter(Objects::nonNull)
                                                           .toList();

        playersToExclude.addAll(duplicatedPlayersToExclude);


        return playersToExclude;

    }

    public List<ThirdPartySitePlayer> getPlayersToNOTExclude(long minBuyPrice, List<ThirdPartySitePlayer> allPlayers) {

        List<ThirdPartySitePlayer> playersToNOTExclude = new ArrayList<>();

        var allNotDuplicatedPlayers = allPlayers.stream()
                                                .filter(player -> allPlayers.stream()
                                                                            .filter(player1 -> Objects.equals(player1.getFutId(), player.getFutId()))
                                                                            .count() == 1)
                                                .toList();

        var notDuplicatedPlayersToNOTExclude = allNotDuplicatedPlayers.stream()
                                                                      .filter(playerPriceMoreThanMinBuyPrice(minBuyPrice))
                                                                      .toList();

        playersToNOTExclude.addAll(notDuplicatedPlayersToNOTExclude);


        var duplicatedPlayers = allPlayers.stream()
                                          .filter(player -> allPlayers.stream()
                                                                      .filter(player1 -> Objects.equals(player1.getFutId(), player.getFutId()))
                                                                      .count() > 1)
                                          .toList();

        var uniquePlayersFutId = duplicatedPlayers.stream()
                                                  .map(ThirdPartySitePlayer::getFutId)
                                                  .collect(Collectors.toSet());

        var duplicatedPlayersToNOTExclude = uniquePlayersFutId.stream().map(playerFutId -> {

                                                                  var duplicatePlayersPair = duplicatedPlayers.stream().filter(player -> Objects.equals(player.getFutId(), playerFutId)).toList();

                                                                  var arePlayersPairToNOTExclude = duplicatePlayersPair.stream()
                                                                                                                       .allMatch(playerPriceMoreThanMinBuyPrice(minBuyPrice));

                                                                  if (arePlayersPairToNOTExclude) {
                                                                      return duplicatePlayersPair.stream().findAny().get();
                                                                  } else {
                                                                      return null;
                                                                  }
                                                              }).filter(Objects::nonNull)
                                                              .toList();

        playersToNOTExclude.addAll(duplicatedPlayersToNOTExclude);
        return playersToNOTExclude;
    }

    Predicate<ThirdPartySitePlayer> playerPriceMoreThanMinBuyPrice(long minBuyPrice) {
        return player -> player.getPrice() > minBuyPrice;
    }

    Predicate<ThirdPartySitePlayer> playerPriceLessThanMinBuyPrice(long minBuyPrice) {
        return player -> player.getPrice() <= minBuyPrice;
    }
}
