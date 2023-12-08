package com.petroandrushchak.steps;

import com.petroandrushchak.fut.model.FutPlayersAttributes;
import com.petroandrushchak.futwiz.models.FutWizRawPlayer;
import com.petroandrushchak.model.fut.Position;
import com.petroandrushchak.model.fut.Quality;
import com.petroandrushchak.model.fut.Rarity;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayer;
import com.petroandrushchak.model.third.party.sites.ThirdPartySitePlayersAttributes;
import com.petroandrushchak.service.fut.FutClubService;
import com.petroandrushchak.service.fut.FutLeagueService;
import com.petroandrushchak.service.fut.FutNationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FutWizMappingSteps {

    @Autowired FutClubService futClubService;
    @Autowired FutLeagueService futLeagueService;
    @Autowired FutNationService futNationService;

    public FutPlayersAttributes mapThirdPartyPlayersAttributesToPlayersAttributes(ThirdPartySitePlayersAttributes futBinPlayersUniqueAttributes) {

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


    public List<ThirdPartySitePlayer> mapNewRawPlayersToPlayers(List<FutWizRawPlayer> rawPlayers) {

        var futWizPlayers = new ArrayList<ThirdPartySitePlayer>();

        rawPlayers.forEach(rawPlayer -> {
            ThirdPartySitePlayer thirdPartySitePlayer = new ThirdPartySitePlayer();
            log.info("Mapping raw player: {}", rawPlayer);

            thirdPartySitePlayer.setThirdPartySiteId(Long.parseLong(rawPlayer.getInternalId()));

            if (rawPlayer.isFUTIdPresent()) {
                thirdPartySitePlayer.setFutId(Long.parseLong(rawPlayer.getFutId()));
            }

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


            futWizPlayers.add(thirdPartySitePlayer);
        });


        return futWizPlayers;
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

    private Pair<Quality, Rarity> getPlayerQualityAndRarity(String qualityAndRarity) {
        var trimmedQualityAndRarity = qualityAndRarity.trim();

        var elements = trimmedQualityAndRarity.split(" ");
        var trimmedElements = Arrays.stream(elements).map(String::trim).toList();

        if (trimmedElements.size() == 2) {
            var goldSilverBronze = getGoldSilverBronze(trimmedElements.get(0));
            var rarity = getRareNonRare(trimmedElements.get(1));
            return Pair.of(goldSilverBronze, rarity);

        } else if (trimmedElements.size() == 1) {
            return getQualityRarityForSpecialPlayers(trimmedElements.get(0));
        } else {
            throw new RuntimeException("Cannot parse quality and rarity: " + qualityAndRarity);
        }
    }

    private ArrayList<Position> getPositions(List<String> positions) {
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

    private ArrayList<Position> getPositions(String position) {
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

    private Pair<Quality, Rarity> getQualityRarityForSpecialPlayers(String specialRarity) {
        return switch (specialRarity) {
            case "gold" -> Pair.of(Quality.GOLD, Rarity.RARE);
            case "gold-nr" -> Pair.of(Quality.GOLD, Rarity.COMMON);
            case "goldif" -> Pair.of(Quality.GOLD, Rarity.TEAM_OF_THE_WEEK);
            case "silver" -> Pair.of(Quality.SILVER, Rarity.RARE);
            case "silver-nr" -> Pair.of(Quality.SILVER, Rarity.COMMON);
            case "silverif" -> Pair.of(Quality.SILVER, Rarity.TEAM_OF_THE_WEEK);
            case "bronze" -> Pair.of(Quality.BRONZE, Rarity.RARE);
            case "bronze-nr" -> Pair.of(Quality.BRONZE, Rarity.COMMON);
            case "bronzeif" -> Pair.of(Quality.BRONZE, Rarity.TEAM_OF_THE_WEEK);

            case "53" -> Pair.of(Quality.SPECIAL, Rarity.CONMEBOL_LIBERTADORES);
            case "54" -> Pair.of(Quality.SPECIAL, Rarity.CONMEBOL_LIBERTADORES_MOTM);
            case "52" -> Pair.of(Quality.SPECIAL, Rarity.CONMEBOL_SUDAMERICANA);
            case "59" -> Pair.of(Quality.SPECIAL, Rarity.CONMEBOL_SUDAMERICANA_MOTM);

            case "8" -> Pair.of(Quality.SPECIAL, Rarity.DOMESTIC_MAN_OF_THE_MATCH);

            case "50" -> Pair.of(Quality.SPECIAL, Rarity.UCL_ROAD_TO_THE_KNOCKOUTS);
            case "105" -> Pair.of(Quality.SPECIAL, Rarity.UECL_ROAD_TO_THE_KNOCKOUTS);
            case "181" -> Pair.of(Quality.SPECIAL, Rarity.UEFA_EURO_ROAD_TO_THE_CUP);
            case "171" -> Pair.of(Quality.SPECIAL, Rarity.UEFA_HEROES_MENS);
            case "172" -> Pair.of(Quality.SPECIAL, Rarity.UEFA_HEROES_WOMENS);
            case "46" -> Pair.of(Quality.SPECIAL, Rarity.UEL_ROAD_TO_THE_KNOCKOUTS);
            case "151" -> Pair.of(Quality.SPECIAL, Rarity.FUT_CENTURIONS);
            case "168" -> Pair.of(Quality.SPECIAL, Rarity.FUT_CENTURIONS_ICON);
            case "futhero" -> Pair.of(Quality.SPECIAL, Rarity.FUT_HEROES);
            case "31" -> Pair.of(Quality.SPECIAL, Rarity.UWCL_ROAD_TO_THE_KNOCKOUTS);
            case "120" -> Pair.of(Quality.SPECIAL, Rarity.WILDCARD_TOKEN);

            case "43" -> Pair.of(Quality.SPECIAL, Rarity.POTM_EPL);
            case "79" -> Pair.of(Quality.SPECIAL, Rarity.POTM_LIGUE1);
            case "114" -> Pair.of(Quality.SPECIAL, Rarity.POTM_SERIEA);
            case "86" -> Pair.of(Quality.SPECIAL, Rarity.POTM_LALIGA);
            case "115" -> Pair.of(Quality.SPECIAL, Rarity.POTM_EREDIVISIE);

            case "icon" -> Pair.of(Quality.SPECIAL, Rarity.ICON);
            case "182" -> Pair.of(Quality.SPECIAL, Rarity.NIKE);
            case "22" -> Pair.of(Quality.SPECIAL, Rarity.TRAILBLAZERS);

            case "91" -> Pair.of(Quality.SPECIAL, Rarity.OBJECTIVES);
            case "51" -> Pair.of(Quality.SPECIAL, Rarity.FLASHBACK);
            case "42" -> Pair.of(Quality.SPECIAL, Rarity.POTM_BUNDESLIGA);
            case "87" -> Pair.of(Quality.SPECIAL, Rarity.SQUAD_FORMATIONS);
            case "150" -> Pair.of(Quality.SPECIAL, Rarity.DYNAMIC_DUO);
            case "157" -> Pair.of(Quality.SPECIAL, Rarity.THUNDERSTUCK_ICON);
            case "33" -> Pair.of(Quality.SPECIAL, Rarity.THUNDERSTUCK);
            case "82" -> Pair.of(Quality.SPECIAL, Rarity.TRIPLE_THREAT_HERO_ICON);
            case "28" -> Pair.of(Quality.SPECIAL, Rarity.TRIPLE_THREAT);

            case "25" -> Pair.of(Quality.SPECIAL, Rarity.SBC_PREMIUM);
            case "57" -> Pair.of(Quality.SPECIAL, Rarity.SHOWDOWN_PLUS_SBC);
            case "58" -> Pair.of(Quality.SPECIAL, Rarity.SHOWDOWN_SBC);

            case "34" -> Pair.of(Quality.SPECIAL, Rarity.FC_PRO);
            case "80" -> Pair.of(Quality.SPECIAL, Rarity.FC_PRO_UPGRADE);

            case "90" -> Pair.of(Quality.SPECIAL, Rarity.MOMENTS);
            case "7" -> Pair.of(Quality.SPECIAL, Rarity.PUNDIT_PICK);

            default -> throw new RuntimeException("Cannot parse special rarity: " + specialRarity);
        };


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

            case "icon" -> Rarity.ICON;
            default -> throw new RuntimeException("Cannot parse special rarity: " + specialRarity);
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

    private Rarity getRareNonRare(String rareNonRare) {
        if (rareNonRare.equalsIgnoreCase("nr")) {
            return Rarity.COMMON;
        } else if (rareNonRare.isBlank()) {
            return Rarity.RARE;
        } else {
            throw new RuntimeException("Cannot parse rarity: " + rareNonRare);
        }
    }

}
