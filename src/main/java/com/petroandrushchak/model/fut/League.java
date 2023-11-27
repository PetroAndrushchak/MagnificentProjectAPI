package com.petroandrushchak.model.fut;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class League {

    Long leagueId;

    String leagueFullName;
    String leagueMediumName;
    String leagueShortName;

}
