package com.petroandrushchak.service;

import com.petroandrushchak.model.fut.Club;
import com.petroandrushchak.model.fut.League;
import com.petroandrushchak.repository.mongo.FUTLeagueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.petroandrushchak.helper.StringHelper.getIntegerAtEndOfString;

@Slf4j
@Service
public class FutLeagueService {

    private final FUTLeagueRepository futLeagueRepository;

    public FutLeagueService(FUTLeagueRepository futLeagueRepository) {
        this.futLeagueRepository = futLeagueRepository;
    }

    public List<League> getAllLeagues() {
        return futLeagueRepository.findAll().getLeagueFullNames().entrySet().stream()
                                  .map(entry -> {
                                      var leagueKey = entry.getKey();
                                      Long leagueId = getIntegerAtEndOfString(leagueKey);
                                      String leagueFullName = entry.getValue();

                                      var result = futLeagueRepository.getLeagueShortAbbreviationById(leagueId);

                                      if (!result.isEmpty()) {
                                          String leagueShortAbbreviation = result.get(0);
                                          return new League(leagueId, leagueFullName, leagueShortAbbreviation);
                                      } else {
                                          return new League(leagueId, leagueFullName, null);
                                      }
                                  }).toList();
    }

    public List<League> getLeaguesByIds(List<Long> toList) {
        var result = futLeagueRepository.findAll();

        return toList.stream().map(id -> {
            var leaguesFullNames = getLeagueFullName(result.getLeagueFullNames(), id);
            var leaguesShortAbbreviationNames = getLeagueShortAbbreviationName(result.getLeagueShortAbbreviations(), id);
            return new League(id, leaguesFullNames, leaguesShortAbbreviationNames);
        }).toList();

    }

    public String getLeagueFullName(Map<String, String> leaguesFullNames, Long id) {
        var foundLeaguesById = leaguesFullNames.entrySet().stream().filter(entry -> {
            var leagueKey = entry.getKey();
            Long leagueId = getIntegerAtEndOfString(leagueKey);
            return leagueId.equals(id);
        }).toList();

        if (foundLeaguesById.isEmpty()) {
            throw new RuntimeException("League with id " + id + " not found");
        }

        if (foundLeaguesById.size() > 1) {
            throw new RuntimeException("Found more than one league with id " + id);
        }

        return foundLeaguesById.get(0).getValue();
    }

    public String getLeagueShortAbbreviationName(Map<String, String> leaguesShortAbbreviationNames, Long id) {
        var foundLeaguesById = leaguesShortAbbreviationNames.entrySet().stream().filter(entry -> {
            var leagueKey = entry.getKey();
            Long leagueId = getIntegerAtEndOfString(leagueKey);
            return leagueId.equals(id);
        }).toList();

        if (foundLeaguesById.isEmpty()) {
            throw new RuntimeException("League with id " + id + " not found");
        }

        if (foundLeaguesById.size() > 1) {
            throw new RuntimeException("Found more than one league with id " + id);
        }

        return foundLeaguesById.get(0).getValue();
    }

}
