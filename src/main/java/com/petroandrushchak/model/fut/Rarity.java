package com.petroandrushchak.model.fut;

import com.fasterxml.jackson.annotation.JsonValue;
import com.petroandrushchak.exceptions.ItemMappingException;

public enum Rarity {

    COMMON("common"),
    RARE("rare"),

    CONMEBOL_LIBERTADORES("conmebol_libertadores"),
    CONMEBOL_LIBERTADORES_MOTM("conmebol_libertadores_motm"),
    CONMEBOL_SUDAMERICANA("conmebol_sudamericana"),
    CONMEBOL_SUDAMERICANA_MOTM("conmebol_sudamericana_motm"),

    DOMESTIC_MAN_OF_THE_MATCH("domestic_man_of_the_match"),
    ICON("icon"),
    NIKE("nike"),
    TEAM_OF_THE_WEEK("team_of_the_week"),
    TRAILBLAZERS("trailblazers"),

    OBJECTIVES("objectives"),

    UCL_ROAD_TO_THE_FINAL("ucl_road_to_the_final"),
    UCL_ROAD_TO_THE_KNOCKOUTS("ucl_road_to_the_knockouts"),
    UECL_ROAD_TO_THE_FINAL("uecl_road_to_the_final"),
    UECL_ROAD_TO_THE_KNOCKOUTS("uecl_road_to_the_knockouts"),
    UEL_ROAD_TO_THE_FINAL("uel_road_to_the_final"),
    UEL_ROAD_TO_THE_KNOCKOUTS("uel_road_to_the_knockouts"),

    UEFA_EURO_ROAD_TO_THE_CUP("uefa_euro_road_to_the_cup"),
    UEFA_HEROES_MENS("uefa_heroes"),
    UEFA_HEROES_WOMENS("uefa_heroes_womens"),

    UWCL_ROAD_TO_THE_KNOCKOUTS("uwcl_road_to_the_knockouts"),

    FUT_CENTURIONS("fut_centurions"),
    FUT_CENTURIONS_ICON("fut_centurions_icon"),
    //Thunderstruck
    THUNDERSTUCK_ICON("fut_thunderstruck_icon"),
    THUNDERSTUCK("fut_thunderstruck"),
    //Triple Threat Hero
    TRIPLE_THREAT_HERO_ICON("fut_triple_threat_hero_icon"),
    //Triple Threat
    TRIPLE_THREAT("fut_triple_threat"),


    FUT_HEROES("fut_heroes"),

    WILDCARD_TOKEN("wildcard_token"),
    FLASHBACK("flashback"),
    SQUAD_FORMATIONS("squad_formations"),
    SHOWDOWN_SBC("showdown_sbc"),
    SHOWDOWN_PLUS_SBC("showdown_plus"),


    //NOT Present for now
    FUT_BIRTHDAY_ICON("fut_birthday_icon"),
    FUT_BIRTHDAY("fut_birthday"),
    FUT_BIRTHDAY_TOKEN("fut_birthday_token"),
    FUTBALLERS("futballers"),

    CHAMPIONS_LEAGUE_MAN_OF_THE_MATCH("champions_league_man_of_the_match"),
    FIFA_WORLD_CUP_PATH_TO_GLORY("fifa_world_cup_path_to_glory"),

    TROPHY_TITANS("trophy_titans"),
    TROPHY_TITANS_ICON("trophy_titans_icon"),


    FUT_FUTURE_STARS("fut_future_stars"),
    FUT_CHAMPIONS_TOTS("fut_champions_tots"),

    FANTASY_FUT("fantasy_fut"),
    FANTASY_FUT_HEROES("fantasy_fut_heroes"),

    ONE_TO_WATCH("one_to_watch"),
    OUT_OF_POSITION("out_of_position"),
    RULE_BREAKERS("rule_breakers"),
    TEAM_OF_THE_YEAR("team_of_the_year"),
    TEAM_OF_THE_SEASON("team_of_the_season"),
    TEAM_OF_THE_SEASON_MOMENTS("team_of_the_season_moments"),
    TOTY_HONOURABLE_MENTIONS("toty_honourable_mentions"),
    TOTY_ICON("toty_icon"),
    FC_PRO("fc_pro"),
    //FC Pro Upgrade
    FC_PRO_UPGRADE("fc_pro_upgrade"),

    CHAMPIONS_RTTL("champions_rttl"),
    CONFERENCE_RTTL("conference_rttl"),
    EUROPA_RTTL("europa_rttl"),

    POTM_EPL("potm_epl"),
    POTM_BUNDESLIGA("potm_bundesliga"),
    POTM_LIGUE1("potm_ligue1"),
    POTM_SERIEA("potm_seriea"),
    POTM_LALIGA("potm_laliga"),
    POTM_EREDIVISIE("potm_eredivisie"),

    WINTER_WILDCARDS("winter_wildcards"),

    MOMENTS("moments"),
    DYNAMIC_DUO("dynamic_duo"),
    SBC_PREMIUM("sbc_premium"),
    PUNDIT_PICK("pundit_pick");

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
