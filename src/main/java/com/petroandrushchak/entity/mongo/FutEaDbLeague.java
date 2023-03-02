package com.petroandrushchak.entity.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Data
@Document("FUTLeague")
public class FutEaDbLeague {

    @Field("league_full_2023")
    Map<String, String> leagueFullNames;

    @Field("league_abbr_5_2023")
    Map<String, String> leagueShortAbbreviations;

    @Field("league_abbr_15_2023")
    Map<String, String> leagueFullAbbreviations;




}
