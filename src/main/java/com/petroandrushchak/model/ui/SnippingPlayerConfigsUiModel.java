package com.petroandrushchak.model.ui;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder(builderMethodName = "anSnippingFilter", toBuilder = true, setterPrefix = "with")
public class SnippingPlayerConfigsUiModel {

    @NotEmpty
    String playerName;

    @NotEmpty
    int playerRating;

    @NotEmpty
    int sellPrice;

}
