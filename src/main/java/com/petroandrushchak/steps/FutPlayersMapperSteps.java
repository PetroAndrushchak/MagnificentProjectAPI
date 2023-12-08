package com.petroandrushchak.steps;

import com.petroandrushchak.fut.model.statistic.PlayerCsvStatisticItem;
import com.petroandrushchak.fut.model.statistic.PlayerStatisticItem;
import com.petroandrushchak.model.fut.PlayerItem;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.service.fut.FutPlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FutPlayersMapperSteps {

    @Autowired FutPlayerService futPlayerService;

    public List<PlayerStatisticItem> mapPlayers(List<PlayerCsvStatisticItem> playersToAnaliseCsvFile) {
        return playersToAnaliseCsvFile.stream()
                                      .map(this::mapPlayer)
                                      .toList();
    }

    public PlayerStatisticItem mapPlayer(PlayerCsvStatisticItem playerCsvStatisticItem) {
        var playerStatisticItem = new PlayerStatisticItem();
        playerStatisticItem.setPlayerName(playerCsvStatisticItem.getPlayerName());
        playerStatisticItem.setRating(playerCsvStatisticItem.getPlayerRating());
        playerStatisticItem.setPossibleSellPrice(playerCsvStatisticItem.getPossibleSellPrice());

        var playerId = playerCsvStatisticItem.getPlayerId();
        var player = futPlayerService.getPlayerById(playerId);

        playerStatisticItem.setId(playerId);
        playerStatisticItem.setPlayerFirstName(player.getFirstName());
        playerStatisticItem.setPlayerLastName(player.getLastName());
        playerStatisticItem.setNickName(player.getNickName());

        return playerStatisticItem;
    }

    public List<PlayerItem> mapThirdPartyPlayers(List<ThirdPartySitePlayer> thirdPartySitePlayers) {
        return thirdPartySitePlayers.stream()
                                    .map(this::mapThirdPartyPlayer)
                                    .toList();
    }

    public PlayerItem mapThirdPartyPlayer(ThirdPartySitePlayer thirdPartySitePlayer) {
        var playerItem = new PlayerItem();
        playerItem.setRating(thirdPartySitePlayer.getRating());
        playerItem.setId(thirdPartySitePlayer.getFutId());

        var playerFromDBOptional = futPlayerService.getPlayerByIdOptional(thirdPartySitePlayer.getFutId());

        if (playerFromDBOptional.isEmpty()) {
            System.out.println(thirdPartySitePlayer.getFutId());
            return null;
        }

        var playerFromDB = playerFromDBOptional.get();


        playerItem.setPlayerFirstName(playerFromDB.getFirstName());
        playerItem.setPlayerLastName(playerFromDB.getLastName());

        playerItem.setPlayerName(playerFromDB.getFirstName() + " " + playerFromDB.getLastName());

        playerItem.setNickName(playerFromDB.getNickName());

        //TODO Finish mapping the rest of the fields

        return playerItem;
    }

    public PlayerItem mapThirdPartyPlayerWithSpecialOption(ThirdPartySitePlayer thirdPartySitePlayer) {
        var playerItem = new PlayerItem();

        var playerFromDBOptional = futPlayerService.getPlayerByIdOptional(thirdPartySitePlayer.getFutId());

        if (playerFromDBOptional.isPresent()) {
            log.info("This is simple player, mapping as a simple player");
            var playerFromDB = playerFromDBOptional.get();

            playerItem.setId(playerFromDB.getId());
            playerItem.setRating(playerFromDB.getRating());
            playerItem.setPlayerFirstName(playerFromDB.getFirstName());
            playerItem.setPlayerLastName(playerFromDB.getLastName());
            playerItem.setPlayerName(playerFromDB.getFirstName() + " " + playerFromDB.getLastName());
            playerItem.setNickName(playerFromDB.getNickName());
            return playerItem;
            //TODO Finish mapping the rest of the fields
        }

        //It can be a special player
        var optionalSimplePlayerFromSpecial = futPlayerService.findSimplePlayerBySpecialPlayer(thirdPartySitePlayer.getFutId());

        if (optionalSimplePlayerFromSpecial.isPresent()) {
            log.info("This is special player, mapping as a special player");
            var simplePlayerFromSpecial = optionalSimplePlayerFromSpecial.get();

            playerItem.setId(simplePlayerFromSpecial.getId());
            playerItem.setRating(simplePlayerFromSpecial.getRating());
            playerItem.setPlayerFirstName(simplePlayerFromSpecial.getFirstName());
            playerItem.setPlayerLastName(simplePlayerFromSpecial.getLastName());
            playerItem.setPlayerName(simplePlayerFromSpecial.getFirstName() + " " + simplePlayerFromSpecial.getLastName());
            playerItem.setNickName(simplePlayerFromSpecial.getNickName());
            return playerItem;
            //TODO Finish mapping the rest of the fields
        }

        throw new IllegalArgumentException("Player with futId " + thirdPartySitePlayer.getFutId() + " not found in DB.");

    }
}
