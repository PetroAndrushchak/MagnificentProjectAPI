package com.petroandrushchak.steps;

import com.petroandrushchak.futbin.models.FutBinPlayer;
import com.petroandrushchak.futbin.models.FutBinPlayersAttributes;
import com.petroandrushchak.futbin.models.FutBinRawPlayer;
import com.petroandrushchak.model.fut.FutPlayersAttributes;
import com.petroandrushchak.model.fut.League;
import com.petroandrushchak.model.fut.Nation;
import com.petroandrushchak.model.fut.Position;
import com.petroandrushchak.model.fut.snipping.filters.*;
import com.petroandrushchak.service.FutBinService;
import com.petroandrushchak.service.FutClubService;
import com.petroandrushchak.service.FutLeagueService;
import com.petroandrushchak.service.FutNationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.petroandrushchak.model.fut.snipping.filters.AttributeType.*;

@Slf4j
@Component
public class FutBinSnippingFiltersSteps {

    public FutBinPlayersAttributes getUniquePlayersAttributes(List<FutBinPlayer> futBinPlayers) {
        var futBinPlayersAttributes = new FutBinPlayersAttributes();

        futBinPlayers.forEach(futBinPlayer -> {
            futBinPlayersAttributes
                    .addClubId(futBinPlayer.getClubId())
                    .addLeagueId(futBinPlayer.getLeagueId())
                    .addNationId(futBinPlayer.getNationId())
                    .addPositions(futBinPlayer.getPositions())
                    .addQuality(futBinPlayer.getQuality())
                    .addRarity(futBinPlayer.getRarity());
        });

        return futBinPlayersAttributes;
    }

    public List<List<Attribute>> getAllPossibleCombinationsForAttributes(List<AttributeType> attributeTypes, FutPlayersAttributes futPlayersAttributes) {
        List<List<AttributeType>> attributesTypeCombinations = generateAttributeTypesCombinations(attributeTypes);
        printAttributeTypesCombinations(attributesTypeCombinations);

        List<List<Attribute>> attributesValuesCombinations = new ArrayList<>();

        for (List<AttributeType> attrTypes : attributesTypeCombinations) {
            List<List<Attribute>> combinationAttributes = createListOfAttributesValues(attrTypes, futPlayersAttributes);
            attributesValuesCombinations.addAll(combinationAttributes);
        }
        printAttributesCombinations(attributesValuesCombinations);
        return attributesValuesCombinations;

    }

    private List<List<Attribute>> createListOfAttributesValues(List<AttributeType> attributeTypes, FutPlayersAttributes futPlayersAttributes) {

        List<List<Attribute>> attributesForCombination = new ArrayList<>();

        attributeTypes.forEach(attributeType -> {
            List<Attribute> newListOfAttributes = getAttributesValuesForType(attributeType, futPlayersAttributes);

            if (attributesForCombination.isEmpty()) {
                newListOfAttributes.forEach(attribute -> {
                    List<Attribute> attributeList = new ArrayList<>();
                    attributeList.add(attribute);
                    attributesForCombination.add(attributeList);
                });

            } else {
                List<List<Attribute>> newAttributesWithCombination = new ArrayList<>();

                for (Attribute newAttribute : newListOfAttributes) {
                    for (List<Attribute> attributeList : attributesForCombination) {
                        List<Attribute> newAttributeList = new ArrayList<>(attributeList);
                        newAttributeList.add(newAttribute);
                        newAttributesWithCombination.add(newAttributeList);
                    }
                }
                attributesForCombination.clear();
                attributesForCombination.addAll(newAttributesWithCombination);
            }
        });

        return attributesForCombination;
    }

    //TODO Add All Possible Attributes
    private List<Attribute> getAttributesValuesForType(AttributeType attributeType, FutPlayersAttributes futPlayersAttributes) {
        List<Attribute> attributes = new ArrayList<>();

        if (attributeType == POSITION) {
            futPlayersAttributes.getPositions().forEach(position -> {
                PositionAttribute positionAttribute = new PositionAttribute();
                positionAttribute.setPosition(position);
                attributes.add(positionAttribute);
            });
        } else if (attributeType == NATION) {
            var nations = futPlayersAttributes.getNations();
            for (Nation nation : nations) {
                NationAttribute nationAttribute = new NationAttribute();
                nationAttribute.setNationId(nation.getNationId());
                nationAttribute.setNationName(nation.getNationName());
                attributes.add(nationAttribute);
            }
        } else if (attributeType == LEAGUE) {
            var leagues = futPlayersAttributes.getLeagues();
            for (League league : leagues) {
                LeagueAttribute leagueAttribute = new LeagueAttribute();
                leagueAttribute.setLeagueId(league.getLeagueId());
                leagueAttribute.setLeagueName(league.getLeagueFullName());
                attributes.add(leagueAttribute);
            }
        } else if (attributeType == CLUB) {
            var clubs = futPlayersAttributes.getClubs();
            for (var club : clubs) {
                ClubAttribute clubAttribute = new ClubAttribute();
                clubAttribute.setClubId(club.getClubId());
                clubAttribute.setClubName(club.getClubLongAbbreviation());
                attributes.add(clubAttribute);
            }
        } else {
            throw new RuntimeException("Unknown attribute type: " + attributeType);
        }

        return attributes;
    }

    //TODO Support for more than 4 attributes, possibly recursion
    public static List<List<AttributeType>> generateAttributeTypesCombinations(List<AttributeType> attributeTypes) {
        List<List<AttributeType>> combinations = new ArrayList<>();

        for (int i = 0; i < attributeTypes.size(); i++) {
            List<AttributeType> combination = Arrays.asList(attributeTypes.get(i));
            combinations.add(combination);

            for (int j = i + 1; j < attributeTypes.size(); j++) {
                List<AttributeType> twoAttributesCombination = Arrays.asList(attributeTypes.get(i), attributeTypes.get(j));
                combinations.add(twoAttributesCombination);

                for (int k = j + 1; k < attributeTypes.size(); k++) {
                    List<AttributeType> threeAttributesCombination = Arrays.asList(attributeTypes.get(i), attributeTypes.get(j), attributeTypes.get(k));
                    combinations.add(threeAttributesCombination);

                    for (int l = k + 1; l < attributeTypes.size(); l++) {
                        List<AttributeType> fourAttributesCombination = Arrays.asList(attributeTypes.get(i), attributeTypes.get(j), attributeTypes.get(k), attributeTypes.get(l));
                        combinations.add(fourAttributesCombination);
                    }
                }
            }
        }

        return combinations;
    }

    private static void printAttributeTypesCombinations(List<List<AttributeType>> combinations) {
        log.info("Total number of combinations: " + combinations.size());
        log.info("Combinations: ");
        for (List<AttributeType> subCombination : combinations) {
            for (AttributeType attribute : subCombination) {
                System.out.print((attribute.name() + " "));
            }
            System.out.println();
        }
    }

    private static void printAttributesCombinations(List<List<Attribute>> combinations) {
        log.info("Total number of combinations: " + combinations.size());
        log.info("Combinations: ");
        for (List<Attribute> subCombination : combinations) {
            for (Attribute attribute : subCombination) {
                System.out.print((attribute.getStringRepresentation() + " "));
            }
            System.out.println();
        }
    }

    public static List<PlayersMatchingAttributes> findAllPlayersMatchingAttributes(List<List<Attribute>> allPossibleAttributesCombinations, List<FutBinPlayer> futBinPlayers) {

        var foundPlayersToSnipe = new ArrayList<PlayersMatchingAttributes>();

        allPossibleAttributesCombinations.forEach(attributesList -> {

            var playersMatchingAttributes = findPlayersMatchingAttributes(attributesList, futBinPlayers);

            if (arePlayersOkayToSnipe(playersMatchingAttributes)) {
                foundPlayersToSnipe.add(new PlayersMatchingAttributes(attributesList, playersMatchingAttributes));
            }
        });

        printPlayersMatchingAttributes(foundPlayersToSnipe);

        return foundPlayersToSnipe;

    }

    private static void printPlayersMatchingAttributes(ArrayList<PlayersMatchingAttributes> foundPlayersToSnipe) {
        log.info("Found players to snipe: ");
        foundPlayersToSnipe.forEach(playersMatchingAttributes -> {
            System.out.print("  Attributes: ");
            var string = playersMatchingAttributes.getAttributes().stream()
                                     .map(Attribute::getShortStringRepresentation)
                                     .collect(Collectors.joining(", "));
            System.out.println(string);

            System.out.println("      Players: ");
            playersMatchingAttributes.getPlayers().forEach(futBinPlayer -> {
                System.out.println("          " + futBinPlayer.getPlayerName() + "  , price -> " + futBinPlayer.getPrice());
            });
        });
    }

    public static List<FutBinPlayer> findPlayersMatchingAttributes(List<Attribute> attributes, List<FutBinPlayer> futBinPlayers) {
        List<FutBinPlayer> playersMatchingAttributes = new ArrayList<>();

        futBinPlayers.forEach(futBinPlayer -> {
            boolean isPlayerMatchingAttributes = true;
            for (Attribute attribute : attributes) {
                if (!isPlayerHasAttribute(futBinPlayer, attribute)) {
                    isPlayerMatchingAttributes = false;
                    break;
                }
            }
            if (isPlayerMatchingAttributes) {
                playersMatchingAttributes.add(futBinPlayer);
            }
        });
        return playersMatchingAttributes;
    }

    public static boolean isPlayerHasAttribute(FutBinPlayer player, Attribute attribute) {

        if (attribute instanceof PositionAttribute positionAttribute) {
            return player.isPlayerHasPosition(positionAttribute.getPosition());
        } else if (attribute instanceof NationAttribute nationAttribute) {
            return player.getNationId().equals(nationAttribute.getNationId());
        } else if (attribute instanceof LeagueAttribute leagueAttribute) {
            return player.getLeagueId().equals(leagueAttribute.getLeagueId());
        } else if (attribute instanceof ClubAttribute clubAttribute) {
            return player.getClubId().equals(clubAttribute.getClubId());
        } else {
            throw new RuntimeException("Unknown attribute"  + attribute);
        }
    }

    public static boolean arePlayersOkayToSnipe(List<FutBinPlayer> players) {

        //Players Number should more than 2
        if (players.isEmpty() || players.size() == 1) {
            return false;
        }

        //Min Players price should be more than 1000
        long minPrice = players.stream()
                               .map(FutBinPlayer::getPrice)
                               .min(Long::compareTo)
                               .orElse(0L);

        if (minPrice < 1000) {
            return false;
        }

        //Find Number of Players with price more than 1000 and more than minPrice with deviation 200

        var count = players.stream()
                           .filter(player -> player.getPrice() >= minPrice)
                           .filter(player -> Math.abs(player.getPrice() - minPrice) <= getPossibleDeviationForPlayerMinPrice(minPrice))
                           .count();

        return count > 1;
    }

    private static long getPossibleDeviationForPlayerMinPrice(long minPrice) {
        if (minPrice < 1000) {
            return 100;
        } else if (minPrice <= 2_000) {
            return 200;
        } else if (minPrice <= 3_000) {
            return 300;
        } else if (minPrice <= 4_000) {
            return 400;
        } else if (minPrice <= 5_000) {
            return 500;
        } else if (minPrice <= 6_000) {
            return 800;
        } else if (minPrice <= 10_000) {
            return 1000;
        } else if (minPrice <= 15_000) {
            return 1200;
        } else if (minPrice <= 20_000) {
            return 1500;
        } else if (minPrice <= 30_000) {
            return 2000;
        } else if (minPrice <= 50_000) {
            return 5000;
        } else if (minPrice <= 100_000) {
            return 7000;
        } else if (minPrice <= 500_000) {
            return 20_000;
        } else if (minPrice <= 1_000_000) {
            return 50_000;
        } else {
            return 100_000;
        }
    }
}
