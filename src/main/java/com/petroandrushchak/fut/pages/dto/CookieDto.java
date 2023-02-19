package com.petroandrushchak.fut.pages.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class CookieDto {

    private final String name;
    private final String value;
    private final String path;
    private final String domain;
    private final Date expiry;
    private final boolean isSecure;
    private final boolean isHttpOnly;
    private final String sameSite;

}
