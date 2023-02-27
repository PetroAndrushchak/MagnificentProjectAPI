package com.petroandrushchak.steps;

import com.petroandrushchak.entity.mongo.FutEaDbPlayer;
import com.petroandrushchak.exceptions.BrowserProcessFutAccountBlocked;
import com.petroandrushchak.exceptions.ItemMappingException;
import com.petroandrushchak.mapper.ui.api.PlayerItemMapper;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.repository.mongo.FUTPlayersRepository;
import com.petroandrushchak.service.BrowserProcessService;
import com.petroandrushchak.service.FutAccountService;
import com.petroandrushchak.view.FutEaAccountView;
import com.petroandrushchak.view.request.PlayerItemRequestBody;
import com.petroandrushchak.view.request.SnippingRequestBody;
import com.petroandrushchak.view.response.SnippingResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class SnippingValidationsSteps {

    @Autowired
    FutAccountService futAccountService;

    @Autowired
    BrowserProcessService browserProcessService;

    @Autowired FUTPlayersRepository futPlayersRepository;

    public FutEaAccountView validateSnippingRequestFutAccount(SnippingRequestBody snippingRequestBody) {

        log.info("Start Validation for Snipping Request: " + snippingRequestBody);

        // Step 1: Validate if Fut Account exists
        var futAccount = futAccountService.getFutAccountById(snippingRequestBody.getFutEaAccountId());
        log.info("Fut Account: " + futAccount);

        // Step 2: Validate if Fut Account is not used in any already running sessions
        var isBrowserProcessRunningForFutAccount = browserProcessService.isAnyBrowserProcessRunningForFutAccount(futAccount);
        if (isBrowserProcessRunningForFutAccount) {
            throw new BrowserProcessFutAccountBlocked(snippingRequestBody.getFutEaAccountId());
        }

        return futAccount;

    }

    public Item validateSnippingRequestItem(SnippingRequestBody snippingRequestBody) {

        //Step 3: Validate fields which are preset are valida from Mongo DB
        FutEaDbPlayer futEaDbPlayer = validatePlayerNameRatingPlayerId(snippingRequestBody.getPlayer());


        // nationality;
        // league;
        // club;

        //Step 3: Validate mapping for Fut Player Item
        var playerItemView = PlayerItemMapper.INSTANCE.playerItemRequestToView(snippingRequestBody.getPlayer(), futEaDbPlayer);
        log.info("Player Item View: " + playerItemView);

        return playerItemView;
    }

    private FutEaDbPlayer validatePlayerNameRatingPlayerId(PlayerItemRequestBody playerItem) {

        var isPlayerIdPresent = playerItem.isPlayerIdPresent();
        var isPlayerRatingPresent = playerItem.isPlayerRatingPresent();
        var isPlayerNamePresent = playerItem.isPlayerNamePresent();

        FutEaDbPlayer futEaDbPlayer = null;

        if (!isPlayerIdPresent && !isPlayerNamePresent) {
            throw new ItemMappingException("Player Id, Player Name", "All fields are not present");
        }

        if (isPlayerIdPresent) {
            var playerId = playerItem.getPlayerId();
            var futPlayers = futPlayersRepository.findByPlayerId(playerId);

            if (futPlayers.isEmpty()) {
                throw new ItemMappingException("Player Id", "Player with id: " + playerId + " not found in DB");
            } else if (futPlayers.size() > 1) {
                throw new ItemMappingException("Player Id", "Player with id: " + playerId + " found more than 1 in DB");
            }

            var futPlayer = futPlayers.get(0);
            if (isPlayerRatingPresent && !Objects.equals(futPlayer.getRating(), playerItem.getPlayerRating())) {
                throw new ItemMappingException("Player Rating", "Player with id: " + playerId + " found in DB with rating: " + futPlayer.getRating());
            }

            if (isPlayerNamePresent) {
                var playerName = playerItem.getPlayerName();
                if (!playerName.equals(futPlayer.getNickName()) && !playerName.equals(futPlayer.getFirstName() + " " + futPlayer.getLastName())) {
                    throw new ItemMappingException("Player Name", "Player with id: " + playerId + " found in DB with name: " + futPlayer.getFirstName() + " " + futPlayer.getLastName() + " or " + futPlayer.getNickName());
                }
            }

            futEaDbPlayer = futPlayer;
        }

        if (isPlayerNamePresent) {
            var playerName = playerItem.getPlayerName();

            var futPlayersByNickName = futPlayersRepository.findByNickname(playerName);
            var futPlayersByFirstLastName = futPlayersRepository.findByFullName(playerName);

            if (futPlayersByFirstLastName.isEmpty() && futPlayersByNickName.isEmpty()) {
                throw new ItemMappingException("Player Name", "Player with name: " + playerName + " not found in DB");
            }

            if (futPlayersByFirstLastName.size() > 1 || futPlayersByNickName.size() > 1) {
                throw new ItemMappingException("Player Name", "Player with name: " + playerName + " found more than 1 in DB");
            }

            if (!futPlayersByNickName.isEmpty() && !futPlayersByFirstLastName.isEmpty()) {
                var futPlayerByNickName = futPlayersByNickName.get(0);
                var futPlayerByFirstLastName = futPlayersByFirstLastName.get(0);

                if (!Objects.equals(futPlayerByNickName.getPlayerId(), futPlayerByFirstLastName.getPlayerId())) {
                    throw new ItemMappingException("Player Name", "Player with name: " + playerName + " found in DB with different player ids: " + futPlayerByNickName.getPlayerId() + " and " + futPlayerByFirstLastName.getPlayerId());
                }
            }

            if (isPlayerRatingPresent && !futPlayersByNickName.isEmpty()) {
                var playerRating = playerItem.getPlayerRating();
                if (!playerRating.equals(futPlayersByNickName.get(0).getRating())) {
                    throw new ItemMappingException("Player Rating", "Player with name: " + playerName + " found in DB with rating: " + futPlayersByNickName.get(0)
                                                                                                                                                           .getRating());
                }
            }

            if (isPlayerRatingPresent && !futPlayersByFirstLastName.isEmpty()) {
                var playerRating = playerItem.getPlayerRating();
                if (!playerRating.equals(futPlayersByFirstLastName.get(0).getRating())) {
                    throw new ItemMappingException("Player Rating", "Player with name: " + playerName + " found in DB with rating: " + futPlayersByFirstLastName.get(0)
                                                                                                                                                                .getRating());
                }
            }

            futEaDbPlayer = futPlayersByNickName.isEmpty() ? futPlayersByFirstLastName.get(0) : futPlayersByNickName.get(0);

        }

        if (isPlayerRatingPresent && !isPlayerNamePresent) {
            throw new ItemMappingException("Player Rating", "Player Rating is not supported without Player Name");
        }

        return futEaDbPlayer;
    }

}


