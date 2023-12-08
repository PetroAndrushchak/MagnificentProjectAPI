package com.petroandrushchak.steps;

import com.petroandrushchak.entity.local.db.FutInternalPlayer;
import com.petroandrushchak.exceptions.BrowserProcessFutAccountBlocked;
import com.petroandrushchak.exceptions.ItemMappingException;
import com.petroandrushchak.mapper.ui.api.PlayerItemMapper;
import com.petroandrushchak.model.fut.Club;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.model.fut.League;
import com.petroandrushchak.model.fut.Nation;
import com.petroandrushchak.service.BrowserProcessService;
import com.petroandrushchak.service.firebase.FutAccountServiceFirebase;
import com.petroandrushchak.service.fut.FutClubService;
import com.petroandrushchak.service.fut.FutLeagueService;
import com.petroandrushchak.service.fut.FutNationService;
import com.petroandrushchak.service.fut.FutPlayerService;
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

    @Autowired FutAccountServiceFirebase futAccountService;

    @Autowired BrowserProcessService browserProcessService;

    @Autowired FutPlayerService futPlayerService;

    @Autowired FutNationService futNationService;
    @Autowired FutLeagueService futLeagueService;

    @Autowired FutClubService futClubService;

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
        FutInternalPlayer futEaDbPlayer = validatePlayerNameRatingPlayerId(snippingRequestBody.getPlayer());
        Optional<Nation> nation = validatePlayerNation(snippingRequestBody.getPlayer());
        Optional<League> league = validatePlayerLeague(snippingRequestBody.getPlayer());
        Optional<Club> club = validatePlayerClub(snippingRequestBody.getPlayer());

        //Step 2: Validate
        // club;

        //Step 3: Validate mapping for Fut Player Item
        var playerItemView = PlayerItemMapper.INSTANCE.playerItemRequestToView(snippingRequestBody.getPlayer(), futEaDbPlayer, nation, league, club);
        log.info("Player Item View: " + playerItemView);

        return playerItemView;
    }

    private Optional<Club> validatePlayerClub(PlayerItemRequestBody player) {
        log.info("Validating Player Club: " + player);

        var isClubIdPresent = player.isClubIdPresent();
        var isClubShortAbbreviationPresent = player.isClubShortAbbreviationPresent();
        var isClubMediumAbbreviationPresent = player.isClubMediumAbbreviationPresent();

        if (isClubIdPresent) {

            var club = futClubService.getClubById(player.getClubId());
            var clubShortAbbreviation = club.getClubShortAbbreviation();
            var clubMediumAbbreviation = club.getClubMediumAbbreviation();
            var clubLongAbbreviation = club.getClubLongAbbreviation();

            if (isClubShortAbbreviationPresent && !player.getClubShortAbbreviation().equals(clubShortAbbreviation)) {
                throw new ItemMappingException("Club Short Abbreviation", "Club with id: " + player.getClubId() + " found in DB with short abbreviation: " + clubShortAbbreviation);
            }

            if (isClubMediumAbbreviationPresent && !player.getClubMediumAbbreviation().equals(clubMediumAbbreviation)) {
                throw new ItemMappingException("Club Medium Abbreviation", "Club with id: " + player.getClubId() + " found in DB with medium abbreviation: " + clubMediumAbbreviation);
            }

            return Optional.of(new Club(player.getClubId(), clubShortAbbreviation, clubMediumAbbreviation, clubLongAbbreviation));
        } else if (isClubShortAbbreviationPresent) {

            var club = futClubService.getClubById(player.getClubId());

            var clubMediumAbbreviation = club.getClubMediumAbbreviation();

            if (isClubMediumAbbreviationPresent && !player.getClubMediumAbbreviation().equals(clubMediumAbbreviation)) {
                throw new ItemMappingException("Club Medium Abbreviation", "Club with short abbreviation: " + player.getClubShortAbbreviation() + " found in DB with medium abbreviation: " + clubMediumAbbreviation);
            }

            return Optional.of(club);
        } else if (isClubMediumAbbreviationPresent) {

            var club = futClubService.getClubById(player.getClubId());
            return Optional.of(club);
        }

        return Optional.empty();
    }

    private Optional<Nation> validatePlayerNation(PlayerItemRequestBody playerItem) {
        log.info("Validating Player Nation: " + playerItem);

        var isNationIdPresent = playerItem.isNationIdPresent();
        var isNationNamePresent = playerItem.isNationNamePresent();
        var isNationAbbreviationPresent = playerItem.isNationAbbreviationPresent();

        if (isNationIdPresent) {
            var foundNationResultOptional = futNationService.getNationByIdOptional(playerItem.getNationId());

            if (foundNationResultOptional.isEmpty())
                throw new ItemMappingException("Nation Id", "Nation with id: " + playerItem.getNationId() + " not found in DB");

            return foundNationResultOptional;
        } else if (isNationAbbreviationPresent) {
            var foundNationIdsResult = futNationService.getNationByAbbreviationOptional(playerItem.getNationAbbreviation());

            if (foundNationIdsResult.isEmpty())
                throw new ItemMappingException("Nation Abbreviation", "Nation with abbreviation: " + playerItem.getNationAbbreviation() + " not found in DB");

            if (isNationNamePresent && !playerItem.getNationName().equals(foundNationIdsResult.get().getNationName())) {
                throw new ItemMappingException("Nation Name", "Nation with abbreviation: " + playerItem.getNationAbbreviation() + " found in DB with name: " + playerItem.getNationName());
            }
            return foundNationIdsResult;

        } else if (isNationNamePresent) {
            var foundNationIdsResult = futNationService.getNationByNameOptional(playerItem.getNationName());

            if (foundNationIdsResult.isEmpty())
                throw new ItemMappingException("Nation Name", "Nation with name: " + playerItem.getNationName() + " not found in DB");

            return foundNationIdsResult;

        }
        return Optional.empty();
    }

    private Optional<League> validatePlayerLeague(PlayerItemRequestBody playerItem) {
        log.info("Validating Player League: " + playerItem);

        var isLeagueIdPresent = playerItem.isLeagueIdPresent();
        var isLeagueNamePresent = playerItem.isLeagueFullNamePresent();
        var isLeagueShortAbbreviationPresent = playerItem.isLeagueShortAbbreviationPresent();

        if (isLeagueIdPresent) {

            var leagueFullName = futLeagueService.getLeagueById(playerItem.getLeagueId()).getLeagueFullName();
            var league = futLeagueService.getLeagueById(playerItem.getLeagueId());
            var leagueShortAbbreviation = league.getLeagueShortName();

            if (isLeagueNamePresent && !playerItem.getLeagueFullName().equals(leagueFullName)) {
                throw new ItemMappingException("League Name", "League with id: " + playerItem.getLeagueId() + " found in DB with name: " + leagueFullName);
            }

            if (isLeagueShortAbbreviationPresent && !playerItem.getLeagueShortAbbreviation()
                                                               .equals(leagueShortAbbreviation)) {
                throw new ItemMappingException("League Abbreviation", "League with id: " + playerItem.getLeagueId() + " found in DB with abbreviation: " + leagueShortAbbreviation);
            }

            return Optional.of(league);
        } else if (isLeagueShortAbbreviationPresent) {
            var foundLeagueIdsResult = futLeagueService.getLeagueIdsByShortName(playerItem.getLeagueShortAbbreviation());

            if (foundLeagueIdsResult.isEmpty())
                throw new ItemMappingException("League Abbreviation", "League with abbreviation: " + playerItem.getLeagueShortAbbreviation() + " not found in DB");
            if (foundLeagueIdsResult.size() > 1)
                throw new ItemMappingException("League Abbreviation", "League with abbreviation: " + playerItem.getLeagueShortAbbreviation() + " found more than 1 in DB");

            var leagueId = foundLeagueIdsResult.get(0);

            var league = futLeagueService.getLeagueById(leagueId);
            var leagueFullName = league.getLeagueFullName();

            if (isLeagueNamePresent && !playerItem.getLeagueFullName().equals(leagueFullName)) {
                throw new ItemMappingException("League Name", "League with abbreviation: " + playerItem.getLeagueShortAbbreviation() + " found in DB with name: " + leagueFullName);
            }

            return Optional.of(league);

        } else if (isLeagueNamePresent) {
            var foundLeagueIdsResult = futLeagueService.getLeagueIdsByFullName(playerItem.getLeagueFullName());

            if (foundLeagueIdsResult.isEmpty())
                throw new ItemMappingException("League Name", "League with name: " + playerItem.getLeagueFullName() + " not found in DB");
            if (foundLeagueIdsResult.size() > 1)
                throw new ItemMappingException("League Name", "League with name: " + playerItem.getLeagueFullName() + " found more than 1 in DB");

            var leagueId = foundLeagueIdsResult.get(0);

            var league = futLeagueService.getLeagueById(leagueId);

            return Optional.of(league);

        } else {
            return Optional.empty();
        }

    }

    private FutInternalPlayer validatePlayerNameRatingPlayerId(PlayerItemRequestBody playerItem) {

        var isPlayerIdPresent = playerItem.isPlayerIdPresent();
        var isPlayerRatingPresent = playerItem.isPlayerRatingPresent();
        var isPlayerNamePresent = playerItem.isPlayerNamePresent();

        FutInternalPlayer futEaDbPlayer = null;

        if (!isPlayerIdPresent && !isPlayerNamePresent) {
            throw new ItemMappingException("Player Id, Player Name", "All fields are not present");
        }

        if (isPlayerIdPresent) {
            var playerId = playerItem.getPlayerId();
            var futPlayers = futPlayerService.getPlayerByIdOptional(playerId);

            if (futPlayers.isEmpty()) {
                throw new ItemMappingException("Player Id", "Player with id: " + playerId + " not found in DB");
            }

            var futPlayer = futPlayers.get();
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

            var futPlayersByNickName = futPlayerService.findPlayersByNickname(playerName);
            var futPlayersByFirstLastName = futPlayerService.findPlayersByFullName(playerName);

            if (futPlayersByFirstLastName.isEmpty() && futPlayersByNickName.isEmpty()) {
                throw new ItemMappingException("Player Name", "Player with name: " + playerName + " not found in DB");
            }

            if (futPlayersByFirstLastName.size() > 1 || futPlayersByNickName.size() > 1) {
                throw new ItemMappingException("Player Name", "Player with name: " + playerName + " found more than 1 in DB");
            }

            if (!futPlayersByNickName.isEmpty() && !futPlayersByFirstLastName.isEmpty()) {
                var futPlayerByNickName = futPlayersByNickName.get(0);
                var futPlayerByFirstLastName = futPlayersByFirstLastName.get(0);

                if (!Objects.equals(futPlayerByNickName.getId(), futPlayerByFirstLastName.getId())) {
                    throw new ItemMappingException("Player Name", "Player with name: " + playerName + " found in DB with different player ids: " + futPlayerByNickName.getId() + " and " + futPlayerByFirstLastName.getId());
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


