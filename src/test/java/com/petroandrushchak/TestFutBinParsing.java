package com.petroandrushchak;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.petroandrushchak.futbin.models.FutBinRawPlayer;
import com.petroandrushchak.futbin.models.FutBinPlayersSearchFilter;
import com.petroandrushchak.futbin.models.MinMaxPrice;
import com.petroandrushchak.futbin.models.Version;
import com.petroandrushchak.futbin.steps.FutBinSteps;
import com.petroandrushchak.service.FutBinService;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TestFutBinParsing {

    @Autowired FutBinSteps futBinSteps;
    @Autowired FutBinService futBinService;

    @Test
    void test() {

        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 60000;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--remote-allow-origins=http://localhost:9222");
        options.setExperimentalOption("debuggerAddress", "localhost:9222");

        System.setProperty("webdriver.chrome.driver", "/Users/pandrushchak.appwell/Workspace/OtherProjects /magnificent-project-api/src/main/resources/chromedriver");
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        ChromeDriver chromeDriver = new ChromeDriver(options);
        WebDriverRunner.setWebDriver(chromeDriver);

        FutBinPlayersSearchFilter searchFilter = new FutBinPlayersSearchFilter();
        searchFilter.setVersion(Version.ALL_GOLD);
        searchFilter.setMinMaxPrice(MinMaxPrice.of(3000, 5000));

        //  Selenide.open("https://nowsecure.nl/");
        var players = futBinSteps.parseAllPlayers(searchFilter);
        futBinService.storePlayersToFile(players);
        System.out.println("test");
    }

    @Test
    void saveFutBinPlayersToCsvFile() {
        List<FutBinRawPlayer> players = new ArrayList<>();

        FutBinRawPlayer futBinRawPlayer = new FutBinRawPlayer();
        futBinRawPlayer.setId("123");
        futBinRawPlayer.setName("Petro");
        futBinRawPlayer.setClubId("123");
        futBinRawPlayer.setClubName("Club");
        futBinRawPlayer.setNationId("123");
        futBinRawPlayer.setNationName("Ukraine");
        futBinRawPlayer.setLeagueId("123");
        futBinRawPlayer.setLeagueName("Premier League");
        futBinRawPlayer.setQualityAndRarity("Gold");
        futBinRawPlayer.setRating("99");
        futBinRawPlayer.setPositions(List.of("ST", "CF"));
        futBinRawPlayer.setPriceText("1000");

        //create another one

        FutBinRawPlayer futBinRawPlayer2 = new FutBinRawPlayer();
        futBinRawPlayer2.setId("123");
        futBinRawPlayer2.setName("Petro");
        futBinRawPlayer2.setClubId("123");
        futBinRawPlayer2.setClubName("Club");
        futBinRawPlayer2.setNationId("123");
        futBinRawPlayer2.setNationName("Ukraine");
        futBinRawPlayer2.setLeagueId("123");
        futBinRawPlayer2.setLeagueName("Premier League");
        futBinRawPlayer2.setQualityAndRarity("Gold");
        futBinRawPlayer2.setRating("99");
        futBinRawPlayer2.setPositions(List.of("ST", "CF"));
        futBinRawPlayer2.setPriceText("1000");

        players.add(futBinRawPlayer);
        players.add(futBinRawPlayer2);

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(FutBinRawPlayer.class).withHeader();
        File file = new File("src/main/resources/futbin/futBinPlayers.csv");

        try {
            // write the list of players to the CSV file, overwriting the existing content
            FileWriter writer = new FileWriter(file, false);
            csvMapper.writer(schema).writeValues(writer).writeAll(players);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
