package com.petroandrushchak.repository.mongo;


import com.petroandrushchak.entity.mongo.FutEaDbClub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.petroandrushchak.helper.StringHelper.getIntegerAtEndOfString;

@Slf4j
@Component
public class FUTClubRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public FutEaDbClub findAll() {
        var result = mongoTemplate.findAll(FutEaDbClub.class);
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            throw new RuntimeException("Club collection is empty");
        }
    }

    public List<String> getClubShortNamesById(Long id) {
        return findAll().getClubShortAbbreviationNames().entrySet().stream()
                        .filter(entry -> {
                            var clubKey = entry.getKey();
                            Long clubId = getIntegerAtEndOfString(clubKey);
                            return clubId.equals(id);
                        }).map(Map.Entry::getValue)
                        .toList();
    }

    public List<String> getClubMediumNamesById(Long id) {
        return findAll().getClubMediumAbbreviationNames().entrySet().stream()
                        .filter(entry -> {
                            var clubKey = entry.getKey();
                            Long clubId = getIntegerAtEndOfString(clubKey);
                            return clubId.equals(id);
                        }).map(Map.Entry::getValue)
                        .toList();
    }

    public List<String> getClubLongNamesById(Long id) {
        return findAll().getClubLongAbbreviationNames().entrySet().stream()
                        .filter(entry -> {
                            var clubKey = entry.getKey();
                            Long clubId = getIntegerAtEndOfString(clubKey);
                            return clubId.equals(id);
                        }).map(Map.Entry::getValue)
                        .toList();
    }

    public List<Long> getClubIdsByShortName(String shortName) {
        return findAll().getClubShortAbbreviationNames().entrySet().stream()
                        .filter(entry -> {
                            var clubKey = entry.getValue();
                            return clubKey.equals(shortName);
                        }).map(entry -> getIntegerAtEndOfString(entry.getKey()))
                        .toList();
    }

    public List<Long> getClubIdsByMediumName(String mediumName) {
        return findAll().getClubMediumAbbreviationNames().entrySet().stream()
                        .filter(entry -> {
                            var clubKey = entry.getValue();
                            return clubKey.equals(mediumName);
                        }).map(entry -> getIntegerAtEndOfString(entry.getKey()))
                        .toList();
    }
}
