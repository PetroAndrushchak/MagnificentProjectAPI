package com.petroandrushchak.view;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "anFutEaAccount", toBuilder = true, setterPrefix = "with")
public class FutEaAccountView {

    Long id;

    String username;

    String eaLogin;
    String eaPassword;

    String eaEmailEmail;
    String eaEmailPassword;

}
