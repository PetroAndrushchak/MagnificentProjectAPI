package com.petroandrushchak.model.validation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SnippingValidationMessage {

    String errorMessage;


    boolean allGood;

}
