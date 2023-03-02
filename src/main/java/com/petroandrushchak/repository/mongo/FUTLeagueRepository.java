package com.petroandrushchak.repository.mongo;

import com.petroandrushchak.entity.mongo.FutEaDbLeague;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.petroandrushchak.helper.StringHelper.getIntegerAtEndOfString;

@Slf4j
@Component
public class FUTLeagueRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public FutEaDbLeague findAll() {
        var result = mongoTemplate.findAll(FutEaDbLeague.class);
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            throw new RuntimeException("League collection is empty");
        }
    }

    public List<String> getLeagueFullNameById(Long id) {
        return findAll().getLeagueFullNames().entrySet().stream()
                        .filter(entry -> {
                            var leagueKey = entry.getKey();
                            Long leagueId = getIntegerAtEndOfString(leagueKey);
                            return leagueId.equals(id);
                        }).map(Map.Entry::getValue)
                        .toList();
    }

    public List<String> getLeagueShortAbbreviationById(Long id) {
        return findAll().getLeagueShortAbbreviations().entrySet().stream()
                        .filter(entry -> {
                            var leagueKey = entry.getKey();
                            Long leagueId = getIntegerAtEndOfString(leagueKey);
                            return leagueId.equals(id);
                        }).map(Map.Entry::getValue)
                        .toList();
    }

    public List<String> getLeagueFullAbbreviationById(Long id) {
        return findAll().getLeagueFullAbbreviations().entrySet().stream()
                        .filter(entry -> {
                            var leagueKey = entry.getKey();
                            Long leagueId = getIntegerAtEndOfString(leagueKey);
                            return leagueId.equals(id);
                        }).map(Map.Entry::getValue)
                        .toList();
    }

    public List<Long> getLeagueIdsByFullName(String fullName) {
        return findAll().getLeagueFullNames().entrySet().stream()
                        .filter(entry -> {
                            var leagueKey = entry.getValue();
                            return leagueKey.equals(fullName);
                        }).map(entry -> getIntegerAtEndOfString(entry.getKey()))
                        .toList();
    }

    public List<Long> getLeagueIdsByShortAbbreviation(String abbreviation) {
        return findAll().getLeagueShortAbbreviations().entrySet().stream()
                        .filter(entry -> {
                            var leagueKey = entry.getValue();
                            return leagueKey.equals(abbreviation);
                        }).map(entry -> getIntegerAtEndOfString(entry.getKey()))
                        .toList();
    }

    public List<Long> getLeagueIdsByFullAbbreviation(String abbreviation) {
        return findAll().getLeagueFullAbbreviations().entrySet().stream()
                        .filter(entry -> {
                            var leagueKey = entry.getValue();
                            return leagueKey.equals(abbreviation);
                        }).map(entry -> getIntegerAtEndOfString(entry.getKey()))
                        .toList();
    }


}
