package com.petroandrushchak.service.fut;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.petroandrushchak.entity.local.db.FuInternalPlayers;
import com.petroandrushchak.entity.local.db.FutInternalPlayer;
import com.petroandrushchak.model.fut.Quality;
import com.petroandrushchak.model.fut.Rarity;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.registry.FutInternalDataRegistry;
import com.petroandrushchak.registry.FutWizDataRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class FutPlayerService {

    HashMap<Long, FutInternalPlayer> playersDBMap;
    List<ThirdPartySitePlayer> futWizAllPLayersDB;

    public FutInternalPlayer getPlayerById(Long id) {
        loadPlayers();

        if (playersDBMap.containsKey(id)) {
            return playersDBMap.get(id);
        } else {
            throw new IllegalArgumentException("Player with ID " + id + " not found in the map.");
        }
    }

    public Optional<FutInternalPlayer> getPlayerByIdOptional(Long id) {
        loadPlayers();

        if (playersDBMap.containsKey(id)) {
            return Optional.of(playersDBMap.get(id));
        } else {
            return Optional.empty();
        }
    }

    public List<FutInternalPlayer> findPlayersByNickname(String nickname) {
        loadPlayers();

        return playersDBMap.values().stream()
                           .filter(player -> player.getNickName().equals(nickname))
                           .toList();
    }

    public List<FutInternalPlayer> findPlayersByFullName(String fullName) {
        loadPlayers();
        return playersDBMap.values().stream()
                           .filter(player -> (player.getFirstName() + " " + player.getLastName()).equals(fullName))
                           .toList();
    }

    public Optional<FutInternalPlayer> findSimplePlayerBySpecialPlayer(Long futId) {
        loadPlayers();

        if (futId == 50540069) {
            System.out.println("sdfsdf");
        }

        var result = futWizAllPLayersDB.stream()
                                       .filter(futWizAllPLayer -> futWizAllPLayer.getFutId() != null)
                                       .filter(futWizAllPLayer -> {
                                           //  log.info("Filtering player with name and rating: " + futWizAllPLayer.getPlayerName() + " " + futWizAllPLayer.getRating());
                                           return futWizAllPLayer.getFutId().equals(futId);
                                       })
                                       .toList();
        if (result.size() > 1) {
            throw new IllegalArgumentException("More than one player with futId " + futId + " found in FutWiz DB.");
        }

        if (result.size() == 0) {
            System.out.println("dsfsdf");
        }

        var foundSpecialPlayer = result.get(0);

        var foundAllPlayersItems = futWizAllPLayersDB.stream()
                                                     .filter(futWizAllPLayer -> futWizAllPLayer.getPlayerName()
                                                                                               .equals(foundSpecialPlayer.getPlayerName()))
                                                     .toList();

        var foundSimplePlayerCards = foundAllPlayersItems.stream()
                                                         .filter(playerSpecialItem -> playerSpecialItem.getQuality() == Quality.GOLD || playerSpecialItem.getQuality() == Quality.SILVER || playerSpecialItem.getQuality() == Quality.BRONZE)
                                                         .filter(playerSpecialItem -> playerSpecialItem.getRarity() == Rarity.COMMON || playerSpecialItem.getRarity() == Rarity.RARE)
                                                         .toList();

        if (foundSimplePlayerCards.size() == 1) {
            log.info("Found simple player card for special player " + foundSpecialPlayer.getPlayerName() + " with futId " + futId);
            return Optional.of(getPlayerById(foundSimplePlayerCards.get(0).getFutId()));
        }

        if (foundSimplePlayerCards.size() > 1) {
            log.info("Found more than one simple player card for special player " + foundSpecialPlayer.getPlayerName() + " with futId " + futId);

            var firstFutId = foundSimplePlayerCards.get(0).getFutId();
            var allFutIdsAreTheSame = foundSimplePlayerCards.stream()
                                                            .allMatch(foundSimplePlayerCard -> foundSimplePlayerCard.getFutId().equals(firstFutId));
            if (allFutIdsAreTheSame) {
                return Optional.of(getPlayerById(firstFutId));
            } else {
                //We have found more than simple card for the player which name is the same with other player, so lets find by club, nation, and league
                var foundPlayerWithTheSameName = foundSimplePlayerCards.stream()
                                                                       .filter(simpleCard -> {
                                                                           return Objects.equals(simpleCard.getClubId(), foundSpecialPlayer.getClubId()) && Objects.equals(simpleCard.getLeagueId(), foundSpecialPlayer.getLeagueId()) && Objects.equals(simpleCard.getNationId(), foundSpecialPlayer.getNationId());
                                                                       })
                                                                       .toList();

                if (foundPlayerWithTheSameName.size() != 1) {
                    throw new RuntimeException("Cannot find simple player for special player " + foundSpecialPlayer.getPlayerName() + " with futId " + futId + " by club, nation, and league");
                } else {
                    return Optional.of(getPlayerById(foundPlayerWithTheSameName.get(0).getFutId()));
                }


            }
        }

        throw new RuntimeException("Cannot find simple player for special player " + foundSpecialPlayer.getPlayerName() + " with futId " + futId);
    }

    @SneakyThrows
    private synchronized void loadPlayers() {
        if (playersDBMap == null) {

            var playerFilePath = FutInternalDataRegistry.playersDataFilePath();
            JsonMapper jsonMapper = new JsonMapper();
            var allPlayers = jsonMapper.readValue(playerFilePath.toFile(), FuInternalPlayers.class);

            this.playersDBMap = new HashMap<>();
            allPlayers.getLegendsPlayers().forEach(this::addPlayer);
            allPlayers.getPlayers().forEach(this::addPlayer);

            futWizAllPLayersDB = jsonMapper.readValue(FutWizDataRegistry.allPlayersFilePath()
                                                                        .toFile(), new TypeReference<List<ThirdPartySitePlayer>>() {
            });
        }
    }

    private void addPlayer(FutInternalPlayer player) {
        Long playerId = player.getId();

        if (playersDBMap.containsKey(playerId)) {
            throw new IllegalArgumentException("Player with ID " + playerId + " already exists in the map.");
        }
        playersDBMap.put(playerId, player);
    }
}
