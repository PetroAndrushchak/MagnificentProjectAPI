package com.petroandrushchak.model.fut;

import com.fasterxml.jackson.annotation.JsonValue;
import com.petroandrushchak.exceptions.ItemMappingException;

public enum Rarity {

    COMMON("common"),
    RARE("rare"),

    CONMEBOL_FOUNDATIONS("conmebol_foundations"),
    CONMEBOL_LIBERTADORES("conmebol_libertadores"),
    CONMEBOL_SUDAMERICANA("conmebol_sudamericana"),

    FUT_BIRTHDAY_ICON("fut_birthday_icon"),
    FUT_BIRTHDAY("fut_birthday"),
    FUT_BIRTHDAY_TOKEN("fut_birthday_token"),
    FUTBALLERS("futballers"),

    DOMESTIC_MAN_OF_THE_MATCH("domestic_man_of_the_match"),
    CHAMPIONS_LEAGUE_MAN_OF_THE_MATCH("champions_league_man_of_the_match"),
    FIFA_WORLD_CUP_PATH_TO_GLORY("fifa_world_cup_path_to_glory"),

    TROPHY_TITANS("trophy_titans"),
    TROPHY_TITANS_ICON("trophy_titans_icon"),

    FUT_CENTURIONS("fut_centurions"),
    FUT_FUTURE_STARS("fut_future_stars"),
    FUT_CHAMPIONS_TOTS("fut_champions_tots"),

    FANTASY_FUT("fantasy_fut"),
    FANTASY_FUT_HEROES("fantasy_fut_heroes"),
    FUT_HEROES("fut_heroes"),

    ICON("icon"),
    ONE_TO_WATCH("one_to_watch"),
    OUT_OF_POSITION("out_of_position"),
    RULE_BREAKERS("rule_breakers"),
    TEAM_OF_THE_WEEK("team_of_the_week"),
    TEAM_OF_THE_YEAR("team_of_the_year"),
    TEAM_OF_THE_SEASON("team_of_the_season"),
    TEAM_OF_THE_SEASON_MOMENTS("team_of_the_season_moments"),
    TOTY_HONOURABLE_MENTIONS("toty_honourable_mentions"),
    TOTY_ICON("toty_icon"),
    UCL_ROAD_TO_THE_FINAL("ucl_road_to_the_final"),
    UCL_ROAD_TO_THE_KNOCKOUTS("ucl_road_to_the_knockouts"),
    UECL_ROAD_TO_THE_FINAL("uecl_road_to_the_final"),
    UECL_ROAD_TO_THE_KNOCKOUTS("uecl_road_to_the_knockouts"),

    CHAMPIONS_RTTL("champions_rttl"),
    CONFERENCE_RTTL("conference_rttl"),
    EUROPA_RTTL("europa_rttl"),

    POTM_EPL("potm_epl"),
    POTM_BUNDESLIGA("potm_bundesliga"),
    POTM_LIGUE1("potm_ligue1"),
    POTM_LALIGA("potm_laliga"),
    OBJECTIVES("objectives"),





    UEL_ROAD_TO_THE_FINAL("uel_road_to_the_final"),
    UEL_ROAD_TO_THE_KNOCKOUTS("uel_road_to_the_knockouts"),

    WINTER_WILDCARDS("winter_wildcards"),
    SHOWDOWN_PLUS("showdown_plus"),
    SHOWDOWN("showdown"),

    WORLD_CUP_HERO("world_cup_hero"),
    WORLD_CUP_HISTORY_MAKERS("world_cup_history_makers"),
    WORLD_CUP_PHENOMS("world_cup_phenoms"),
    WORLD_CUP_STORIES("world_cup_stories"),
    WORLD_CUP_ROAD_TO_THE_WORLD_CUP("world_cup_road_to_the_world_cup"),
    WORLD_CUP_SHOWDOWN_PLUS("world_cup_showdown_plus"),
    WORLD_CUP_SHOWDOWN("world_cup_showdown"),
    WORLD_CUP_STAR("world_cup_star"),
    WORLD_CUP_PLAYER("world_cup_player"),
    WORLD_CUP_ICON("world_cup_icon"),
    WORLD_CUP_PTG("world_cup_ptg"),
    WORLD_CUP_TOKEN("world_cup_token"),

    WORLD_CUP_TEAM_OF_THE_TOURNAMENT("world_cup_team_of_the_tournament"),
    MOMENTS("moments"),
    DYNAMIC_DUO("dynamic_duo"),
    FLASHBACK("flashback"),


    SBC_PREMIUM("sbc_premium");

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

    @JsonValue
    public String getValue() {
        return apiKey;
    }
}
