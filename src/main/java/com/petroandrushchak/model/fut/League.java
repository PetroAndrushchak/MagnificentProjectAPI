package com.petroandrushchak.model.fut;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class League {

    Long leagueId;
    String leagueFullName;
    String leagueShortAbbreviation;


    //League FullAbbreviation can also be address, that value is stored in DB

}
