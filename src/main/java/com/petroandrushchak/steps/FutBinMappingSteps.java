package com.petroandrushchak.steps;

import com.petroandrushchak.fut.model.statistic.PlayerCsvStatisticItem;
import com.petroandrushchak.futbin.models.FutBinNewRawPlayer;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayersAttributes;
import com.petroandrushchak.fut.model.FutPlayersAttributes;
import com.petroandrushchak.model.fut.Position;
import com.petroandrushchak.model.fut.Quality;
import com.petroandrushchak.model.fut.Rarity;
import com.petroandrushchak.service.FutLeagueService;
import com.petroandrushchak.service.FutNationService;
import com.petroandrushchak.service.fut.FutClubServiceInternal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FutBinMappingSteps {

    @Autowired FutClubServiceInternal futClubService;
    @Autowired FutLeagueService futLeagueService;
    @Autowired FutNationService futNationService;

    public FutPlayersAttributes mapFutBinPlayersAttributesToPlayersAttributes(ThirdPartySitePlayersAttributes futBinPlayersUniqueAttributes) {

        var playersAttributes = new FutPlayersAttributes();

        playersAttributes.addClubs(futClubService.getClubsByIds(futBinPlayersUniqueAttributes.getClubIds()
                                                                                             .stream()
                                                                                             .toList()));
        playersAttributes.addLeagues(futLeagueService.getLeaguesByIds(futBinPlayersUniqueAttributes.getLeagueIds()
                                                                                                   .stream()
                                                                                                   .toList()));
        playersAttributes.addNations(futNationService.getNationsByIds(futBinPlayersUniqueAttributes.getNationIds()
                                                                                                   .stream()
                                                                                                   .toList()));

        playersAttributes.addPositions(futBinPlayersUniqueAttributes.getPositions().stream().toList());
        playersAttributes.addQualities(futBinPlayersUniqueAttributes.getQualities().stream().toList());
        playersAttributes.addRarities(futBinPlayersUniqueAttributes.getRarities().stream().toList());

        return playersAttributes;
    }

    public List<PlayerCsvStatisticItem> mapFutBinPlayersToCsvStatisticItems(List<ThirdPartySitePlayer> thirdPartySitePlayers) {
        return thirdPartySitePlayers.stream().map(futBinPlayer -> {
                    PlayerCsvStatisticItem playerCsvStatisticItem = new PlayerCsvStatisticItem();
                    var league = futLeagueService.getLeagueById(Math.toIntExact(futBinPlayer.getLeagueId()));
                    playerCsvStatisticItem.setLeagueName(league.getLeagueShortAbbreviation());
                    playerCsvStatisticItem.setPlayerName(futBinPlayer.getPlayerName());
                    playerCsvStatisticItem.setPlayerRating(futBinPlayer.getRating());
                    playerCsvStatisticItem.setPlayerId(futBinPlayer.getFutId());
                    playerCsvStatisticItem.setPossibleSellPrice(futBinPlayer.getPrice());
                    return playerCsvStatisticItem;
                }
        ).toList();
    }

    public List<ThirdPartySitePlayer> mapNewRawPlayersToPlayers(List<FutBinNewRawPlayer> rawPlayers) {

        var futBinPlayers = new ArrayList<ThirdPartySitePlayer>();

        rawPlayers.forEach(rawPlayer -> {
            ThirdPartySitePlayer thirdPartySitePlayer = new ThirdPartySitePlayer();
            log.info("Mapping raw player: {}", rawPlayer);

            thirdPartySitePlayer.setThirdPartySiteId(Long.parseLong(rawPlayer.getInternalId()));
            thirdPartySitePlayer.setFutId(Long.parseLong(rawPlayer.getFutId()));

            thirdPartySitePlayer.setPlayerName(rawPlayer.getName());
            thirdPartySitePlayer.setRating(Integer.parseInt(rawPlayer.getRating()));
            thirdPartySitePlayer.setClubId(Long.parseLong(rawPlayer.getClubId()));
            thirdPartySitePlayer.setNationId(Long.parseLong(rawPlayer.getNationId()));
            thirdPartySitePlayer.setLeagueId(Long.parseLong(rawPlayer.getLeagueId()));

            var qualityRarityPair = getPlayerQualityAndRarity(String.join(" ", rawPlayer.getQualityAndRarity()));

            thirdPartySitePlayer.setQuality(qualityRarityPair.getLeft());
            thirdPartySitePlayer.setRarity(qualityRarityPair.getRight());

            var mainPositions = getPositions(rawPlayer.getMainPosition());
            thirdPartySitePlayer.setMainPositions(mainPositions);

            var positions = getPositions(rawPlayer.getOtherPositions());
            thirdPartySitePlayer.setOtherPositions(positions);

            var price = parsePrice(rawPlayer.getPriceText());
            thirdPartySitePlayer.setPrice(price);


            futBinPlayers.add(thirdPartySitePlayer);
        });


        return futBinPlayers;
    }

    public long parsePrice(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        input = input.trim();
        long multiplier = 1;
        char lastChar = input.charAt(input.length() - 1);

        if (lastChar == 'K' || lastChar == 'k') {
            multiplier = 1000;
            input = input.substring(0, input.length() - 1);
        } else if (lastChar == 'M' || lastChar == 'm') {
            multiplier = 1000000;
            input = input.substring(0, input.length() - 1);
        }

        try {
            return (long) (Double.parseDouble(input) * multiplier);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Cannot parse price: " + input);
        }
    }

    private List<Position> getPositions(List<String> positions) {
        var positionsList = new ArrayList<Position>();

        if (positions == null || positions.isEmpty()) {
            return positionsList;
        }

        positions.forEach(position -> {
            var foundPosition = Position.fromKey(position);
            positionsList.add(foundPosition);

            if (foundPosition.isAttacker() && !positionsList.contains(Position.ATTACKERS)) {
                positionsList.add(Position.ATTACKERS);
            } else if (foundPosition.isMidfielder() && !positionsList.contains(Position.MIDFIELDERS)) {
                positionsList.add(Position.MIDFIELDERS);
            } else if (foundPosition.isDefender() && !positionsList.contains(Position.DEFENDERS)) {
                positionsList.add(Position.DEFENDERS);
            }

        });

        return positionsList;
    }

    private List<Position> getPositions(String position) {
        var positionsList = new ArrayList<Position>();

        var foundPosition = Position.fromKey(position);
        positionsList.add(foundPosition);

        if (foundPosition.isAttacker() && !positionsList.contains(Position.ATTACKERS)) {
            positionsList.add(Position.ATTACKERS);
        } else if (foundPosition.isMidfielder() && !positionsList.contains(Position.MIDFIELDERS)) {
            positionsList.add(Position.MIDFIELDERS);
        } else if (foundPosition.isDefender() && !positionsList.contains(Position.DEFENDERS)) {
            positionsList.add(Position.DEFENDERS);
        }

        return positionsList;
    }

    private Pair<Quality, Rarity> getPlayerQualityAndRarity(String qualityAndRarity) {
        var trimmedQualityAndRarity = qualityAndRarity.trim();

        var elements = trimmedQualityAndRarity.split(" ");
        var trimmedElements = Arrays.stream(elements).map(String::trim).toList();

        if (trimmedElements.size() == 2) {
            var goldSilverBronze = getGoldSilverBronze(trimmedElements.get(0));
            var rarity = getRareNonRare(trimmedElements.get(1));
            return Pair.of(goldSilverBronze, rarity);

        } else if (trimmedElements.size() == 3) {

            var goldSilverBronze = getGoldSilverBronze(trimmedElements.get(1));
            var specialRarity = getSpecialRarity(trimmedElements.get(0));

            if (specialRarity == Rarity.TEAM_OF_THE_WEEK) {
                return Pair.of(goldSilverBronze, Rarity.TEAM_OF_THE_WEEK);
            } else {
                return Pair.of(Quality.SPECIAL, specialRarity);
            }

        } else {
            throw new RuntimeException("Cannot parse quality and rarity: " + qualityAndRarity);
        }

    }

    private Rarity getSpecialRarity(String specialRarity) {
        return switch (specialRarity) {
            case "if" -> Rarity.TEAM_OF_THE_WEEK;
            case "motm" -> Rarity.DOMESTIC_MAN_OF_THE_MATCH;
            case "ucl_motm" -> Rarity.CHAMPIONS_LEAGUE_MAN_OF_THE_MATCH;
            case "trophy_titans_icon" -> Rarity.TROPHY_TITANS_ICON;
            case "trophy_titans" -> Rarity.TROPHY_TITANS;
            case "bd_icon" -> Rarity.FUT_BIRTHDAY_ICON;
            case "fut-bd" -> Rarity.FUT_BIRTHDAY;
            case "futballers" -> Rarity.FUTBALLERS;
            case "bd_token" -> Rarity.FUT_BIRTHDAY_TOKEN;
            case "fantasy_hero" -> Rarity.FANTASY_FUT_HEROES;
            case "fantasy" -> Rarity.FANTASY_FUT;
            case "future_stars" -> Rarity.FUT_FUTURE_STARS;
            case "ucl_rttf" -> Rarity.UCL_ROAD_TO_THE_FINAL;
            case "ucl_live" -> Rarity.CHAMPIONS_RTTL;
            case "europa_live" -> Rarity.EUROPA_RTTL;
            case "conference" -> Rarity.CONFERENCE_RTTL;
            case "uel_rttf" -> Rarity.UEL_ROAD_TO_THE_FINAL;
            case "uecl_rttf" -> Rarity.UECL_ROAD_TO_THE_FINAL;
            case "toty_icon" -> Rarity.TOTY_ICON;
            case "centurions" -> Rarity.FUT_CENTURIONS;
            case "heroes" -> Rarity.FUT_HEROES;
            case "otw" -> Rarity.ONE_TO_WATCH;
            case "sbc_premium" -> Rarity.SBC_PREMIUM;
            case "out_of_position" -> Rarity.OUT_OF_POSITION;
            case "halloween" -> Rarity.RULE_BREAKERS;
            case "f_moment" -> Rarity.MOMENTS;
            case "dynamic_duo" -> Rarity.DYNAMIC_DUO;
            case "sbc_flashback" -> Rarity.FLASHBACK;

            case "showdown_plus" -> Rarity.SHOWDOWN_PLUS_SBC;
            case "winter_wildcards" -> Rarity.WINTER_WILDCARDS;

            case "potm_epl" -> Rarity.POTM_EPL;
            case "potm_bundesliga" -> Rarity.POTM_BUNDESLIGA;
            case "potm_ligue1" -> Rarity.POTM_LIGUE1;
            case "potm_laliga" -> Rarity.POTM_LALIGA;
            case "objective_reward" -> Rarity.OBJECTIVES;

            case "libertadores_b" -> Rarity.CONMEBOL_LIBERTADORES;
            case "sudamericana" -> Rarity.CONMEBOL_SUDAMERICANA;
            case "trailblazers" -> Rarity.TRAILBLAZERS;

            case "icon" -> Rarity.ICON;
            default -> throw new RuntimeException("Cannot parse special rarity: " + specialRarity);
        };
    }

    private Rarity getRareNonRare(String rareNonRare) {
        return switch (rareNonRare) {
            case "rare" -> Rarity.RARE;
            case "non-rare" -> Rarity.COMMON;
            default -> throw new RuntimeException("Cannot parse rarity: " + rareNonRare);
        };
    }

    public Quality getGoldSilverBronze(String goldSilverBronze) {
        return switch (goldSilverBronze) {
            case "gold" -> Quality.GOLD;
            case "silver" -> Quality.SILVER;
            case "bronze" -> Quality.BRONZE;
            default -> throw new RuntimeException("Cannot parse quality: " + goldSilverBronze);
        };
    }
}
