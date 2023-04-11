package com.petroandrushchak.service;

import com.petroandrushchak.model.fut.Club;
import com.petroandrushchak.repository.mongo.FUTClubRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.petroandrushchak.helper.StringHelper.getIntegerAtEndOfString;

@Slf4j
@Service
public class FutClubService {

    private final FUTClubRepository futClubRepository;

    public FutClubService(FUTClubRepository futClubRepository) {
        this.futClubRepository = futClubRepository;
    }

    public List<Club> getClubsByIds(List<Long> ids) {
        var result = futClubRepository.findAll();

        return ids.stream().map(id -> {
            var clubsLongAbbreviationNames = getCLubLongAbbreviation(result.getClubLongAbbreviationNames(), id);
            var clubsShortNames = getClubShortName(result.getClubShortAbbreviationNames(), id);
            var clubsMediumNames = getClubMediumName(result.getClubMediumAbbreviationNames(), id);
            return new Club(id, clubsShortNames, clubsMediumNames, clubsLongAbbreviationNames);
        }).toList();

    }

    public List<Club> getAllClubs() {
        return futClubRepository.findAll().getClubLongAbbreviationNames().entrySet().stream()
                                .map(entry -> {
                                    var clubKey = entry.getKey();
                                    Long clubId = getIntegerAtEndOfString(clubKey);
                                    String clubLongAbbreviation = entry.getValue();

                                    var clubShortNames = futClubRepository.getClubShortNamesById(clubId);
                                    var clubMediumNames = futClubRepository.getClubMediumNamesById(clubId);

                                    var clubShortName = clubShortNames.isEmpty() ? null : clubShortNames.get(0);
                                    var clubMediumName = clubMediumNames.isEmpty() ? null : clubMediumNames.get(0);

                                    return new Club(clubId, clubShortName, clubMediumName, clubLongAbbreviation);

                                }).toList();
    }

    public String getCLubLongAbbreviation(Map<String, String> clubsLongAbbreviationNames, Long id) {
        var foundClubsById = clubsLongAbbreviationNames.entrySet().stream().filter(entry -> {
            var clubKey = entry.getKey();
            Long clubId = getIntegerAtEndOfString(clubKey);
            return clubId.equals(id);
        }).toList();

        if (foundClubsById.isEmpty()) {
            throw new RuntimeException("Club with id " + id + " not found");
        }

        if (foundClubsById.size() > 1) {
            throw new RuntimeException("Found more than one club with id " + id);
        }

        return foundClubsById.get(0).getValue();
    }

    public String getClubShortName(Map<String, String> clubsShortAbbreviationNames, Long id) {
        var foundClubsById = clubsShortAbbreviationNames.entrySet().stream().filter(entry -> {
            var clubKey = entry.getKey();
            Long clubId = getIntegerAtEndOfString(clubKey);
            return clubId.equals(id);
        }).toList();

        if (foundClubsById.isEmpty()) {
            throw new RuntimeException("Club with id " + id + " not found");
        }

        if (foundClubsById.size() > 1) {
            throw new RuntimeException("Found more than one club with id " + id);
        }

        return foundClubsById.get(0).getValue();
    }

    public String getClubMediumName(Map<String, String> clubsMediumAbbreviationNames, Long id) {
        var foundClubsById = clubsMediumAbbreviationNames.entrySet().stream().filter(entry -> {
            var clubKey = entry.getKey();
            Long clubId = getIntegerAtEndOfString(clubKey);
            return clubId.equals(id);
        }).toList();

        if (foundClubsById.isEmpty()) {
            throw new RuntimeException("Club with id " + id + " not found");
        }

        if (foundClubsById.size() > 1) {
            throw new RuntimeException("Found more than one club with id " + id);
        }

        return foundClubsById.get(0).getValue();
    }
}
