package com.petroandrushchak.steps;

import com.petroandrushchak.entity.mongo.FutEaDbPlayer;
import com.petroandrushchak.exceptions.BrowserProcessFutAccountBlocked;
import com.petroandrushchak.exceptions.ItemMappingException;
import com.petroandrushchak.mapper.ui.api.PlayerItemMapper;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.model.fut.Nation;
import com.petroandrushchak.repository.mongo.FUTNationRepository;
import com.petroandrushchak.repository.mongo.FUTPlayersRepository;
import com.petroandrushchak.service.BrowserProcessService;
import com.petroandrushchak.service.FutAccountService;
import com.petroandrushchak.view.FutEaAccountView;
import com.petroandrushchak.view.request.PlayerItemRequestBody;
import com.petroandrushchak.view.request.SnippingRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class SnippingValidationsSteps {

    @Autowired
    FutAccountService futAccountService;

    @Autowired
    BrowserProcessService browserProcessService;

    @Autowired FUTPlayersRepository futPlayersRepository;
    @Autowired FUTNationRepository futNationRepository;

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

        //Step 1: Validate fields which are preset are valid from Mongo DB
        FutEaDbPlayer futEaDbPlayer = validatePlayerNameRatingPlayerId(snippingRequestBody.getPlayer());
        Optional<Nation> nation = validatePlayerNation(snippingRequestBody.getPlayer());


        //Step 2: Validate Nation (if present

        // league;
        // club;

        //Step 3: Validate mapping for Fut Player Item
        var playerItemView = PlayerItemMapper.INSTANCE.playerItemRequestToView(snippingRequestBody.getPlayer(), futEaDbPlayer, nation);
        log.info("Player Item View: " + playerItemView);

        return playerItemView;
    }

    private Optional<Nation> validatePlayerNation(PlayerItemRequestBody playerItem) {
        log.info("Validating Player Nation: " + playerItem);

        var isNationIdPresent = playerItem.isNationIdPresent();
        var isNationNamePresent = playerItem.isNationNamePresent();
        var isNationAbbreviationPresent = playerItem.isNationAbbreviationPresent();

        if (isNationIdPresent) {
            var foundNationNamesResult = futNationRepository.getNationNamesById(playerItem.getNationId());
            var foundNationAbbreviationsResult = futNationRepository.getNationAbbreviationsById(playerItem.getNationId());

            if (foundNationNamesResult.isEmpty())
                throw new ItemMappingException("Nation Id", "Nation with id: " + playerItem.getNationId() + " not found in DB");
            if (foundNationNamesResult.size() > 1)
                throw new ItemMappingException("Nation Id", "Nation with id: " + playerItem.getNationId() + " found more than 1 in DB");
            if (foundNationAbbreviationsResult.isEmpty())
                throw new ItemMappingException("Nation Id", "Nation with id: " + playerItem.getNationId() + " not found in DB");
            if (foundNationAbbreviationsResult.size() > 1)
                throw new ItemMappingException("Nation Id", "Nation with id: " + playerItem.getNationId() + " found more than 1 in DB");

            var nationName = foundNationNamesResult.get(0);
            var nationAbbreviation = foundNationAbbreviationsResult.get(0);

            if (isNationNamePresent && !playerItem.getNationName().equals(nationName)) {
                throw new ItemMappingException("Nation Name", "Nation with id: " + playerItem.getNationId() + " found in DB with name: " + nationName);
            }

            if (isNationAbbreviationPresent && !playerItem.getNationAbbreviation().equals(nationAbbreviation)) {
                throw new ItemMappingException("Nation Abbreviation", "Nation with id: " + playerItem.getNationId() + " found in DB with abbreviation: " + nationAbbreviation);
            }

            return Optional.of(new Nation(playerItem.getNationId(), nationAbbreviation, nationName));
        } else if (isNationAbbreviationPresent) {
            var foundNationIdsResult = futNationRepository.getNationIdsByAbbreviation(playerItem.getNationAbbreviation());

            if (foundNationIdsResult.isEmpty()) throw new ItemMappingException("Nation Abbreviation", "Nation with abbreviation: " + playerItem.getNationAbbreviation() + " not found in DB");
            if (foundNationIdsResult.size() > 1) throw new ItemMappingException("Nation Abbreviation", "Nation with abbreviation: " + playerItem.getNationAbbreviation() + " found more than 1 in DB");

            var nationId = foundNationIdsResult.get(0);

            var foundNationNamesResult = futNationRepository.getNationNamesById(nationId);
            if (foundNationNamesResult.isEmpty()) throw new ItemMappingException("Nation Abbreviation", "Nation with abbreviation: " + playerItem.getNationAbbreviation() + " not found in DB");
            if (foundNationNamesResult.size() > 1) throw new ItemMappingException("Nation Abbreviation", "Nation with abbreviation: " + playerItem.getNationAbbreviation() + " found more than 1 in DB");

            var nationName = foundNationNamesResult.get(0);

            if (isNationNamePresent && !playerItem.getNationName().equals(nationName))
                throw new ItemMappingException("Nation Name", "Nation with abbreviation: " + playerItem.getNationAbbreviation() + " found in DB with name: " + nationName);


            return Optional.of(new Nation(nationId, playerItem.getNationAbbreviation(), nationName));

        } else if (isNationNamePresent) {
            var foundNationIdsResult = futNationRepository.getNationIdsByName(playerItem.getNationName());

            if (foundNationIdsResult.isEmpty()) throw new ItemMappingException("Nation Name", "Nation with name: " + playerItem.getNationName() + " not found in DB");
            if (foundNationIdsResult.size() > 1) throw new ItemMappingException("Nation Name", "Nation with name: " + playerItem.getNationName() + " found more than 1 in DB");

            var nationId = foundNationIdsResult.get(0);

            var foundNationAbbreviationsResult = futNationRepository.getNationAbbreviationsById(nationId);
            if (foundNationAbbreviationsResult.isEmpty()) throw new ItemMappingException("Nation Name", "Nation with name: " + playerItem.getNationName() + " not found in DB");
            if (foundNationAbbreviationsResult.size() > 1) throw new ItemMappingException("Nation Name", "Nation with name: " + playerItem.getNationName() + " found more than 1 in DB");

            var nationAbbreviation = foundNationAbbreviationsResult.get(0);

            return Optional.of(new Nation(nationId, nationAbbreviation, playerItem.getNationName()));

        } else {
            return Optional.empty();
        }
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


