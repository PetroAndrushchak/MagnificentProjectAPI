package com.petroandrushchak.service;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import lombok.experimental.UtilityClass;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@UtilityClass
public class LocalBrowserHelper {

    public static void connectToAlreadyOpenedBrowser() {
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
    }
}
