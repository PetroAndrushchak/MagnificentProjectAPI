package com.petroandrushchak.view;

import com.petroandrushchak.entity.Status;
import com.petroandrushchak.model.fut.Item;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BrowserProcessView {

    Long id;
    String futEaAccountId;
    Item item;
    Status status;

}
