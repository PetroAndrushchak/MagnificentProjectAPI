package com.petroandrushchak.service.fut;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.petroandrushchak.helper.StringHelper;
import com.petroandrushchak.model.fut.League;
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
public class FutLeagueService {

    HashMap<Long, League> leaguesMap;
    List<League> leaguesList;

    public List<Long> getLeagueIdsByShortName(String shortName) {
        return leaguesList.stream()
                          .filter(league -> league.getLeagueShortName().equals(shortName))
                          .map(League::getLeagueId)
                          .toList();
    }

    public List<Long> getLeagueIdsByFullName(String fullName) {
        return leaguesList.stream()
                          .filter(league -> league.getLeagueFullName().equals(fullName))
                          .map(League::getLeagueId)
                          .toList();
    }

    public List<League> getLeaguesByIds(List<Long> ids) {
        loadLeagues();
        return ids.stream().map(this::getLeagueById).toList();
    }

    public League getLeagueById(Long id) {
        loadLeagues();

        if (leaguesMap.containsKey(id)) {
            return leaguesMap.get(id);
        } else {
            throw new IllegalArgumentException("League with ID " + id + " not found in the map.");
        }
    }

    @SneakyThrows
    private synchronized void loadLeagues() {
        if (leaguesMap == null) {
            leaguesMap = new HashMap<>();
            var leagueFilePath = FutInternalDataRegistry.futWebInternalData();
            JsonMapper jsonMapper = new JsonMapper();
            HashMap<String, String> futInternalData = jsonMapper.readValue(leagueFilePath.toFile(), new TypeReference<HashMap<String, String>>() {
            });

            //Get All String with global.leagueFull. and the beginning
            var fullNamesLeagues = futInternalData.entrySet().stream()
                                                  .filter((entry) -> entry.getKey().startsWith("global.leagueFull"))
                                                  .map(entry -> {
                                                      long id = StringHelper.getIntegerAtEndOfString(entry.getKey());
                                                      return new LeagueNameIdPair(id, entry.getValue());
                                                  }).toList();

            var mediumNamesLeagues = futInternalData.entrySet().stream()
                                                    .filter((entry) -> entry.getKey().startsWith("global.leagueabbr15"))
                                                    .map(entry -> {
                                                        long id = StringHelper.getIntegerAtEndOfString(entry.getKey());
                                                        return new LeagueNameIdPair(id, entry.getValue());
                                                    }).toList();

            var shortNamesLeagues = futInternalData.entrySet().stream()
                                                   .filter((entry) -> entry.getKey().startsWith("global.leagueabbr5"))
                                                   .map(entry -> {
                                                       long id = StringHelper.getIntegerAtEndOfString(entry.getKey());
                                                       return new LeagueNameIdPair(id, entry.getValue());
                                                   }).toList();

            var mappedLeagues = fullNamesLeagues.stream()
                                                .map(fullNameLeague -> {
                                                    var leagueId = fullNameLeague.getId();
                                                    var fullName = fullNameLeague.getName();

                                                    var mediumNameWithGivenId = mediumNamesLeagues.stream()
                                                                                                  .filter(mediumNameLeague -> mediumNameLeague.getId() == leagueId)
                                                                                                  .toList();

                                                    if (mediumNameWithGivenId.size() > 1) {
                                                        throw new RuntimeException("League with id " + leagueId + " has more than one medium name");
                                                    }

                                                    var mediumNameValue = mediumNameWithGivenId.isEmpty() ? null : mediumNameWithGivenId.get(0)
                                                                                                                                        .getName();

                                                    var shortNameWithGivenId = shortNamesLeagues.stream()
                                                                                                .filter(shortNameLeague -> shortNameLeague.getId() == leagueId)
                                                                                                .toList();

                                                    if (shortNameWithGivenId.size() > 1) {
                                                        throw new RuntimeException("League with id " + leagueId + " has more than one short name");
                                                    }

                                                    var shortNameValue = shortNameWithGivenId.isEmpty() ? null : shortNameWithGivenId.get(0)
                                                                                                                                     .getName();

                                                    return new League(leagueId, fullName, mediumNameValue, shortNameValue);

                                                }).toList();
            leaguesMap = mappedLeagues.stream()
                                      .collect(HashMap::new, (map, league) -> map.put(league.getLeagueId(), league), HashMap::putAll);
            leaguesList = mappedLeagues;

        }
    }

    @Data
    @AllArgsConstructor
    @Getter
    private static class LeagueNameIdPair {
        long id;
        String name;
    }

}
