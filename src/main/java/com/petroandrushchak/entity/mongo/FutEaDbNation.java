package com.petroandrushchak.entity.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Data
@Document("FUTNation")
public class FutEaDbNation {

    @Field("search_nation_name")
    Map<String, String > nationNames;

    @Field("search_nation_abbr")
    Map<String, String > nationAbbreviations;

    @Field("nation_abbr_byID")
    Map<String, String > nationAbbreviationsById;

}
