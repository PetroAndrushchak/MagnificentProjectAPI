package com.petroandrushchak.unit.tests;

import com.petroandrushchak.fut.api.TransfermarketSearchResponse;
import com.petroandrushchak.helper.JsonParser;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
public class ParseAuctionDataToObject {

    @SneakyThrows
    @Test
    void parseAuctionDataToObject() {
        String content = Files.readString(Paths.get("/Users/pandrushchak.appwell/Workspace/MagnificentProjectAPI/test.json"));


        JsonParser.parseFromString(content, TransfermarketSearchResponse.class);

    }
}
