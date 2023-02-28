package com.petroandrushchak.model.fut;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Nation {

    Long nationId;
    String nationAbbreviation;
    String nationName;

}
