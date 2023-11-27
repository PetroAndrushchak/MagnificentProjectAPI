package com.petroandrushchak.service.fut;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.petroandrushchak.helper.StringHelper;
import com.petroandrushchak.model.fut.Club;
import com.petroandrushchak.registry.FutInternalDataRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class FutClubServiceInternal {

    HashMap<Long, Club> clubsMap;
    List<Club> clubsList;

    public List<Club> getClubsByIds(List<Long> ids) {
        loadClubs();

        return ids.stream().map(this::getClubById).toList();
    }

    public Club getClubById(Long id) {
        loadClubs();

        if (clubsMap.containsKey(id)) {
            return clubsMap.get(id);
        } else {
            throw new IllegalArgumentException("Club with ID " + id + " not found in the map.");
        }
    }

    @SneakyThrows
    private synchronized void loadClubs() {
        if (clubsMap == null) {
            clubsMap = new HashMap<>();
            var clubFilePath = FutInternalDataRegistry.futWebInternalData();
            JsonMapper jsonMapper = new JsonMapper();
            HashMap<String, String> futInternalData = jsonMapper.readValue(clubFilePath.toFile(), new TypeReference<HashMap<String, String>>() {
            });

            //Get All String with global.teamabbr15 and the begin
            var longNamesClubs = futInternalData.entrySet().stream()
                                                .filter((entry) -> entry.getKey().startsWith("global.teamabbr15"))
                                                .map(entry -> {
                                                    long id = StringHelper.getIntegerAtEndOfString(entry.getKey());
                                                    return new ClubNameIdPair(id, entry.getValue());
                                                }).toList();

            var shortNamesClubs = futInternalData.entrySet().stream()
                                                 .filter((entry) -> entry.getKey().startsWith("global.teamabbr3"))
                                                 .map(entry -> {
                                                     long id = StringHelper.getIntegerAtEndOfString(entry.getKey());
                                                     return new ClubNameIdPair(id, entry.getValue());
                                                 }).toList();

            var mediumNamesClubs = futInternalData.entrySet().stream()
                                                  .filter((entry) -> entry.getKey().startsWith("global.teamabbr10"))
                                                  .map(entry -> {
                                                      long id = StringHelper.getIntegerAtEndOfString(entry.getKey());
                                                      return new ClubNameIdPair(id, entry.getValue());
                                                  }).toList();

            var mappedClubs = longNamesClubs.stream()
                                            .map(longNameClub -> {
                                                var id = longNameClub.getId();
                                                var longName = longNameClub.getName();

                                                var shortNameClubsWithGivenId = shortNamesClubs.stream()
                                                                                               .filter(shortNameClub -> shortNameClub.getId() == id)
                                                                                               .toList();
                                                if (shortNameClubsWithGivenId.size() > 1) {
                                                    throw new IllegalArgumentException("There are more than one short name for club with id " + id);
                                                }

                                                var shortName = shortNameClubsWithGivenId.isEmpty() ? null : shortNameClubsWithGivenId.get(0)
                                                                                                                                      .getName();

                                                var mediumNameClubsWithGivenId = mediumNamesClubs.stream()
                                                                                                 .filter(mediumNameClub -> mediumNameClub.getId() == id)
                                                                                                 .toList();

                                                if (mediumNameClubsWithGivenId.size() > 1) {
                                                    throw new IllegalArgumentException("There are more than one medium name for club with id " + id);
                                                }

                                                var mediumName = mediumNameClubsWithGivenId.isEmpty() ? null : mediumNameClubsWithGivenId.get(0)
                                                                                                                                         .getName();

                                                return new Club(id, shortName, mediumName, longName);
                                            }).toList();

            clubsMap = mappedClubs.stream()
                                  .collect(HashMap::new, (map, club) -> map.put(club.getClubId(), club), HashMap::putAll);
            clubsList = mappedClubs;
        }
    }

    @Data
    @AllArgsConstructor
    @Getter
    private static class ClubNameIdPair {
        long id;
        String name;
    }
}
