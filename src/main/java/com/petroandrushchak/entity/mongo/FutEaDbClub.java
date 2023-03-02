package com.petroandrushchak.entity.mongo;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Data
@Document("FUTClub")
public class FutEaDbClub {

    @Field("team_abbr_3_2023")
    Map<String, String> clubShortAbbreviationNames;

    @Field("team_abbr_10_2023")
    Map<String, String> clubMediumAbbreviationNames;

    @Field("team_abbr_15_2023")
    Map<String, String> clubLongAbbreviationNames;
}
