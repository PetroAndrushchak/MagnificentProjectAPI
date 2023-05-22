package com.petroandrushchak.service;

import com.petroandrushchak.entity.mongo.FutEaDbNation;
import com.petroandrushchak.model.fut.Nation;
import com.petroandrushchak.repository.mongo.FUTNationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.petroandrushchak.helper.StringHelper.getDigitFromString;
import static com.petroandrushchak.helper.StringHelper.getIntegerAtEndOfString;

@Slf4j
@Service
public class FutNationService {

    private final FUTNationRepository futNationRepository;

    public FutNationService(FUTNationRepository futNationRepository) {
        this.futNationRepository = futNationRepository;
    }

    public List<Nation> getNationsByIds(List<Long> ids) {
        FutEaDbNation futEaDbNation = futNationRepository.findAll();
        return ids.stream().map(id -> {
            var nationName = getNationName(futEaDbNation.getNationNames(), id);
            var nationAbbreviation = getNationAbbreviation(futEaDbNation.getNationAbbreviationsById(), id);
            return new Nation(id, nationAbbreviation, nationName);
        }).toList();

    }

    public Nation getNationById(int id) {
        var result = futNationRepository.findAll();
        var nationName = getNationName(result.getNationNames(), (long) id);
        var nationAbbreviation = getNationAbbreviation(result.getNationAbbreviationsById(), (long) id);
        return new Nation((long) id, nationAbbreviation, nationName);
    }


    public List<Nation> getAllNations() {
        FutEaDbNation futEaDbNation = futNationRepository.findAll();
        return futEaDbNation.getNationNames().entrySet().stream()
                            .map(entry -> {
                                var nationKey = entry.getKey();
                                Long nationId = getDigitFromString(nationKey);
                                String nationName = entry.getValue();
                                String nationAbbreviation = futEaDbNation.getNationAbbreviationsById().get(nationKey);
                                return new Nation(nationId, nationAbbreviation, nationName);
                            }).toList();
    }

    public String getNationName(Map<String, String> nationNames, Long id) {
        var foundNationsById = nationNames.entrySet().stream().filter(entry -> {
            var nationKey = entry.getKey();
            Long nationId = getIntegerAtEndOfString(nationKey);
            return nationId.equals(id);
        }).toList();

        if (foundNationsById.isEmpty()) {
            throw new RuntimeException("Nation with id " + id + " not found");
        }

        if (foundNationsById.size() > 1) {
            throw new RuntimeException("Found more than one nation with id " + id);
        }

        return foundNationsById.get(0).getValue();
    }

    public String getNationAbbreviation(Map<String, String> nationAbbreviationsById, Long id) {
        var foundNationsById = nationAbbreviationsById.entrySet().stream().filter(entry -> {
            var nationKey = entry.getKey();
            Long nationId = getIntegerAtEndOfString(nationKey);
            return nationId.equals(id);
        }).toList();

        if (foundNationsById.isEmpty()) {
            throw new RuntimeException("Nation with id " + id + " not found");
        }

        if (foundNationsById.size() > 1) {
            throw new RuntimeException("Found more than one nation with id " + id);
        }

        return foundNationsById.get(0).getValue();
    }
}
