package com.petroandrushchak.model.fut;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Club {

    Long clubId;

    String clubShortAbbreviation;
    String clubMediumAbbreviation;
    String clubLongAbbreviation;

}
