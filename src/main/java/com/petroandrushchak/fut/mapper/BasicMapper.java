package com.petroandrushchak.fut.mapper;

import com.petroandrushchak.fut.pages.dto.CookieDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.openqa.selenium.Cookie;

@Mapper
public interface BasicMapper {

    BasicMapper INSTANCE = Mappers.getMapper(BasicMapper.class);

    CookieDto convert(Cookie cookie);


    default Cookie convert(CookieDto cookie) {
        return new Cookie(
                cookie.getName(),
                cookie.getValue(),
                cookie.getDomain(),
                cookie.getPath(),
                cookie.getExpiry(),
                cookie.isSecure(),
                cookie.isHttpOnly(),
                cookie.getSameSite());
    }


}
