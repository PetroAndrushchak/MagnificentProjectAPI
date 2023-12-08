package com.petroandrushchak.service.fut;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.petroandrushchak.model.fut.Nation;
import com.petroandrushchak.registry.FutInternalDataRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.petroandrushchak.helper.StringHelper.getIntegerAtEndOfString;

@Slf4j
@Service
public class FutNationService {

    HashMap<Long, Nation> nationsMap;
    List<Nation> nationList;

    public List<Nation> getNationsByIds(List<Long> ids) {
        loadNations();
        return ids.stream().map(this::getNationById).toList();
    }

    public Nation getNationById(Long id) {
        loadNations();

        if (nationsMap.containsKey(id)) {
            return nationsMap.get(id);
        } else {
            throw new IllegalArgumentException("Nation with ID " + id + " not found in the map.");
        }
    }

    public Optional<Nation> getNationByIdOptional(Long id) {
        loadNations();

        if (nationsMap.containsKey(id)) {
            return Optional.of(nationsMap.get(id));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Nation> getNationByAbbreviationOptional(String abbreviation) {
        loadNations();

        return nationList.stream()
                         .filter(nation -> nation.getNationAbbreviation().equals(abbreviation))
                         .findFirst();
    }

    public Optional<Nation> getNationByNameOptional(String name) {
        loadNations();

        return nationList.stream()
                         .filter(nation -> nation.getNationName().equals(name))
                         .findFirst();
    }

    @SneakyThrows
    private synchronized void loadNations() {
        if (nationsMap == null) {
            nationsMap = new HashMap<>();
            var nationFilePath = FutInternalDataRegistry.futWebInternalData();
            JsonMapper jsonMapper = new JsonMapper();
            HashMap<String, String> futInternalData = jsonMapper.readValue(nationFilePath.toFile(), new TypeReference<HashMap<String, String>>() {
            });

            var fullNamesNationMap = futInternalData.entrySet().stream()
                                                    .filter(entry -> entry.getKey().contains("search.nationName.nation"))
                                                    .map(entry -> {
                                                        var nationKey = entry.getKey();
                                                        long nationId = getIntegerAtEndOfString(nationKey);
                                                        String nationName = entry.getValue();
                                                        return new NationNameIdPair(nationId, nationName);
                                                    }).toList();

            var abbreviationsNationMap = futInternalData.entrySet().stream()
                                                        .filter(entry -> entry.getKey().contains("search.nationAbbr"))
                                                        .map(entry -> {
                                                            var nationKey = entry.getKey();
                                                            long nationId = getIntegerAtEndOfString(nationKey);
                                                            String nationAbbreviation = entry.getValue();
                                                            return new NationNameIdPair(nationId, nationAbbreviation);
                                                        }).toList();

            var mappedNations = fullNamesNationMap.stream()
                                                  .map(nationNameIdPair -> {
                                                      var nationId = nationNameIdPair.getId();
                                                      var nationName = nationNameIdPair.getName();

                                                      var nationAbbreviationWithGivenId = abbreviationsNationMap.stream()
                                                                                                                .filter(nationNameIdPair1 -> nationNameIdPair1.getId() == nationId)
                                                                                                                .collect(Collectors.toList());
                                                      if (nationAbbreviationWithGivenId.size() != 1) {
                                                          throw new RuntimeException("Nation with id " + nationId + " not found");
                                                      }

                                                      var nationAbbreviation = nationAbbreviationWithGivenId.get(0).getName();

                                                      return new Nation(nationId, nationAbbreviation, nationName);
                                                  }).toList();

            nationsMap = mappedNations.stream().collect(HashMap::new, (map, nation) -> map.put(nation.getNationId(), nation), HashMap::putAll);
            nationList = mappedNations;

        }
    }

    @Data
    @AllArgsConstructor
    @Getter
    private static class NationNameIdPair {
        long id;
        String name;
    }
}
