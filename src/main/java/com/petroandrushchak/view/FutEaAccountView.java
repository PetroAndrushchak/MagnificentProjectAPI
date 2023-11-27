package com.petroandrushchak.view;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "anFutEaAccount", toBuilder = true, setterPrefix = "with")
public class FutEaAccountView {

    String id;

    String username;

    String eaLogin;
    String eaPassword;

    String eaEmailEmail;
    String eaEmailPassword;

}
