package com.petroandrushchak.view.request;

import com.petroandrushchak.model.fut.PlayerItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SnippingRequestBody {

    @Valid
    @NotNull(message = "The Fut EA Account is required.")
    @Min(value = 1, message = "The Fut EA Account must be greater than 0.")
    Long futEaAccountId;

    @Valid
    @NotNull(message = "The player Item is required.")
    PlayerItem playerItem;

}
