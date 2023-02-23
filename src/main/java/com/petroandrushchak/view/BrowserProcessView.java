package com.petroandrushchak.view;

import com.petroandrushchak.entity.Status;
import com.petroandrushchak.model.fut.PlayerItem;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BrowserProcessView {

    Long id;
    Long futEaAccountId;
    PlayerItem playerItem;
    Status status;

}
