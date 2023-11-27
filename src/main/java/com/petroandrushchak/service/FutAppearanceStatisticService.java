package com.petroandrushchak.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.petroandrushchak.fut.model.statistic.PlayerCsvStatisticItem;
import com.petroandrushchak.helper.ProjectHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;

@Slf4j
@Service
public class FutAppearanceStatisticService {

    private static final String APPEARANCE_STATISTIC_FILE_NAME = "/database/apearance_players_statistic.csv";

    Path filePath = Paths.get(ProjectHelper.getRootProjectFolderPath(), APPEARANCE_STATISTIC_FILE_NAME);

    @SneakyThrows
    public List<PlayerCsvStatisticItem> getPlayersWithoutAppearanceRating(int numberOfPlayers, long maxPrice) {
        log.info("Getting players without appearance rating, with limit {}", numberOfPlayers);
        var mapper = CsvMapper.builder()
                              .enable(CsvGenerator.Feature.ALWAYS_QUOTE_EMPTY_STRINGS)
                              .build();
        var schema = mapper.schemaFor(PlayerCsvStatisticItem.class)
                           .withHeader()
                           .withColumnSeparator(',')
                           .withComments();

        List<PlayerCsvStatisticItem> playersWithoutAppearanceRating = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (MappingIterator<PlayerCsvStatisticItem> iterator = mapper.readerWithTypedSchemaFor(PlayerCsvStatisticItem.class)
                                                                          .with(schema)
                                                                          .readValues(reader)) {
                var existingPlayers = iterator.readAll();
                playersWithoutAppearanceRating.addAll(existingPlayers);
            }
        }

        return playersWithoutAppearanceRating.stream()
                                             .filter(playerCsvStatisticItem -> playerCsvStatisticItem.getPlayerAppearanceRating() == null)
                                             .filter(playerCsvStatisticItem -> playerCsvStatisticItem.getPossibleSellPrice() <= maxPrice)
                                             .limit(numberOfPlayers)
                                             .toList();
    }

    @SneakyThrows
    public void updateAppearanceStatistic(List<PlayerCsvStatisticItem> playerCsvStatisticItems) {
        log.info("Updating appearance statistic");
        var mapper = CsvMapper.builder()
                              .enable(CsvGenerator.Feature.ALWAYS_QUOTE_EMPTY_STRINGS)
                              .build();
        var schema = mapper.schemaFor(PlayerCsvStatisticItem.class)
                           .withHeader()
                           .withColumnSeparator(',')
                           .withComments();

        //Get Already Existing Players
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (MappingIterator<PlayerCsvStatisticItem> iterator = mapper.readerWithTypedSchemaFor(PlayerCsvStatisticItem.class)
                                                                          .with(schema)
                                                                          .readValues(reader)) {
                var existingPlayers = iterator.readAll();

                var updatedVersionOfPlayers = new ArrayList<>(existingPlayers);

                var newPlayersToAdd = playerCsvStatisticItems.stream()
                                                             .filter(playerCsvItem -> {
                                                                 var playerExistOptional = existingPlayers.stream()
                                                                                                          .filter(existingPlayer -> existingPlayer.getPlayerId()
                                                                                                                                                  .equals(playerCsvItem.getPlayerId()))
                                                                                                          .findFirst();
                                                                 return playerExistOptional.isEmpty();
                                                             }).toList();
                updatedVersionOfPlayers.addAll(newPlayersToAdd);

                var existingPlayersToUpdate = playerCsvStatisticItems.stream()
                                                                     .filter(playerCsvItem -> {
                                                                         var playerExistOptional = existingPlayers.stream()
                                                                                                                  .filter(existingPlayer -> existingPlayer.getPlayerId()
                                                                                                                                                          .equals(playerCsvItem.getPlayerId()))
                                                                                                                  .findFirst();
                                                                         return playerExistOptional.isPresent();
                                                                     }).toList();

                updatedVersionOfPlayers.forEach(playerCsvItem -> {
                    existingPlayersToUpdate.forEach(playerCsvItemToUpdate -> {
                        if (playerCsvItem.getPlayerId().equals(playerCsvItemToUpdate.getPlayerId())) {
                            playerCsvItem.setPossibleSellPrice(playerCsvItemToUpdate.getPossibleSellPrice());
                        }
                    });
                });


                var sortedPlayers = updatedVersionOfPlayers.stream()
                                                           .sorted(Comparator.comparing(PlayerCsvStatisticItem::getPlayerAppearanceRating, Comparator.nullsFirst(Comparator.naturalOrder()))
                                                                             .reversed()
                                                                             .thenComparing(comparing(PlayerCsvStatisticItem::getPossibleSellPrice).reversed()))
                                                           .toList();

                ObjectWriter myObjectWriter = mapper.writer(schema);
                myObjectWriter.writeValue(filePath.toFile(), sortedPlayers);
            }
        }
    }
}
