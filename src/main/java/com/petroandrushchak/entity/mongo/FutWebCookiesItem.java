package com.petroandrushchak.entity.mongo;

import com.petroandrushchak.fut.pages.dto.CookieDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document("FutWebCookiesItem")
public class FutWebCookiesItem {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("eaFutEmailAddress")
    private String eaFutEmail;

    private LocalDateTime dateTimeAdded;

    List<CookieDto> cookieDto;

}
