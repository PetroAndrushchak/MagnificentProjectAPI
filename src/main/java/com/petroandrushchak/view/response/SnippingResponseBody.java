package com.petroandrushchak.view.response;

import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.Data;

@Data
public class SnippingResponseBody {

    Item item;
    String futEaAccountId;

}
