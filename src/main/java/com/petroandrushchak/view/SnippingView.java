package com.petroandrushchak.view;

import com.petroandrushchak.entity.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SnippingView {

    Long id;
    String futAccountId;
    String futEaAccountLogin;
    Status status;
}
