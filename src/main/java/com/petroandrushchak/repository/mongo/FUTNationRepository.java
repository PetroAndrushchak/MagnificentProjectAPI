package com.petroandrushchak.repository.mongo;

import com.petroandrushchak.entity.mongo.FutEaDbNation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.petroandrushchak.helper.StringHelper.getDigitFromString;
import static com.petroandrushchak.helper.StringHelper.getIntegerAtEndOfString;


@Slf4j
@Component
public class FUTNationRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public FutEaDbNation findAll() {
        var result = mongoTemplate.findAll(FutEaDbNation.class);
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            throw new RuntimeException("Nation collection is empty");
        }
    }

    public List<String> getNationNamesById(Long id) {
        return findAll().getNationNames().entrySet().stream()
                        .filter(entry -> {
                            var nationKey = entry.getKey();
                            Long nationId = getDigitFromString(nationKey);
                            return nationId.equals(id);
                        }).map(Map.Entry::getValue)
                        .toList();
    }

    public List<String> getNationAbbreviationsById(Long id) {
        return findAll().getNationAbbreviationsById().entrySet().stream()
                        .filter(entry -> {
                            var nationKey = entry.getKey();
                            Long nationId = getIntegerAtEndOfString(nationKey);
                            return nationId.equals(id);
                        }).map(Map.Entry::getValue)
                        .toList();
    }

    public List<Long> getNationIdsByAbbreviation(String abbreviation) {
        return findAll().getNationAbbreviationsById().entrySet().stream()
                        .filter(entry -> {
                            var nationKey = entry.getValue();
                            return nationKey.equals(abbreviation);
                        }).map(entry -> getIntegerAtEndOfString(entry.getKey()))
                        .toList();
    }


    public List<Long> getNationIdsByName(String nationName) {
        return findAll().getNationNames().entrySet().stream()
                        .filter(entry -> {
                            var nationKey = entry.getValue();
                            return nationKey.equals(nationName);
                        }).map(entry -> getDigitFromString(entry.getKey()))
                        .toList();
    }
}
