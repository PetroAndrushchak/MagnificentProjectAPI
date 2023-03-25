package com.petroandrushchak;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.petroandrushchak.futbin.models.FutBinPlayersSearchFilter;
import com.petroandrushchak.futbin.models.MinMaxPrice;
import com.petroandrushchak.futbin.models.Version;
import com.petroandrushchak.futbin.steps.FutBinSteps;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestFutBinParsing {

    @Autowired FutBinSteps futBinSteps;

    @Test
    void test() {

        Configuration.browserSize = "1920x1080";
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--remote-allow-origins=http://localhost:9222");
        options.setExperimentalOption("debuggerAddress","localhost:9222");

        System.setProperty("webdriver.chrome.driver","/Users/pandrushchak.appwell/Workspace/OtherProjects /magnificent-project-api/src/main/resources/chromedriver");
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        ChromeDriver chromeDriver = new ChromeDriver(options);
        WebDriverRunner.setWebDriver(chromeDriver);

        FutBinPlayersSearchFilter searchFilter = new FutBinPlayersSearchFilter();
        searchFilter.setVersion(Version.ALL_GOLD);
        searchFilter.setMinMaxPrice(MinMaxPrice.of(3000, 5000));

      //  Selenide.open("https://nowsecure.nl/");
        futBinSteps.parseAllPlayers(searchFilter);

        System.out.println("test");
    }
}
