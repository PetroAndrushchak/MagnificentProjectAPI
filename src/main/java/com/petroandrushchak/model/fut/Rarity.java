package com.petroandrushchak.model.fut;

import com.petroandrushchak.exceptions.ItemMappingException;

public enum Rarity {

    COMMON("common"),
    RARE("rare"),
    CONMEBOL_LIBERTADORES("conmebol_libertadores"),
    CONMEBOL_SUDAMERICANA("conmebol_sudamericana"),
    DOMESTIC_MAN_OF_THE_MATCH("domestic_man_of_the_match"),
    FIFA_WORLD_CUP_PATH_TO_GLORY("fifa_world_cup_path_to_glory"),

    FUT_CENTURIONS("fut_centurions"),
    FUT_FUTURE_STARS("fut_future_stars"),
    FUT_HEROES("fut_heroes"),
    ICON("icon"),
    ONE_TO_WATCH("one_to_watch"),
    OUT_OF_POSITION("out_of_position"),
    RULE_BREAKERS("rule_breakers"),
    TEAM_OF_THE_WEEK("team_of_the_week"),
    TEAM_OF_THE_YEAR("team_of_the_year"),
    TOTY_HONOURABLE_MENTIONS("toty_honourable_mentions"),
    TOTY_ICON("toty_icon"),
    UCL_ROAD_TO_THE_FINAL("ucl_road_to_the_final"),
    UCL_ROAD_TO_THE_KNOCKOUTS("ucl_road_to_the_knockouts"),
    UECL_ROAD_TO_THE_FINAL("uecl_road_to_the_final"),
    UECL_ROAD_TO_THE_KNOCKOUTS("uecl_road_to_the_knockouts"),

    UEL_ROAD_TO_THE_FINAL("uel_road_to_the_final"),
    UEL_ROAD_TO_THE_KNOCKOUTS("uel_road_to_the_knockouts"),

    WINTER_WILDCARDS("winter_wildcards"),
    WORLD_CUP_HERO("world_cup_hero"),
    WORLD_CUP_TEAM_OF_THE_TOURNAMENT("world_cup_team_of_the_tournament");

    private final String apiKey;

    Rarity(String apiKey) {
        this.apiKey = apiKey;
    }

    public static Rarity fromApiKey(String value) {
        for (Rarity enumValue : values()) {
            if (enumValue.apiKey.equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new ItemMappingException("Rarity", "Rarity is not found for api key: " + value);
    }

    public String getValue() {
        return apiKey;
    }
    }
