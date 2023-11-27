package com.petroandrushchak.service;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Slf4j
@UtilityClass
public class LocalBrowserHelper {

    public static void connectToAlreadyOpenedBrowser() {

       // WebDriverManager.chromedriver().browserVersion("118.0.5940.0").setup();

        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 5000;
        ChromeOptions options = new ChromeOptions();
     //   Configuration.browserVersion = "118.0.5993.117";

        //set chrome driver path
     //   System.setProperty("webdriver.chrome.driver", "/Users/pandrushchak.appwell/Workspace/MagnificentProjectAPI/chromedriver");

        options.addArguments("--disable-blink-features=AutomationControlled", "--no-sandbox", "--disable-dev-shm-usage");
        options.setExperimentalOption("debuggerAddress", "localhost:9429");
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        ChromeDriver chromeDriver = new ChromeDriver(options);
        WebDriverRunner.setWebDriver(chromeDriver);
    }

    public static boolean isTabWithUrlOpened(String url) {
        return WebDriverRunner.getWebDriver().getCurrentUrl().contains(url);
    }

    public static boolean isTabWithUrlPresent(String url) {
        var windowHandles = WebDriverRunner.getWebDriver().getWindowHandles();
        for (String windowHandle : windowHandles) {
            WebDriverRunner.getWebDriver().switchTo().window(windowHandle);
            if (isTabWithUrlOpened(url)) {
                return true;
            }
        }
        return false;
    }

    public static void openTabWithUrlIfNotPresent(String url) {
        if (isTabWithUrlOpened(url)) {
            log.info("Tab with url {} is already opened", url);
            return;
        }

        if (isTabWithUrlPresent(url)) {
            log.info("Tab with url {} is already present", url);
            return;
        }
        log.info("Tab with url {} is not present. Opening url", url);
        Selenide.open(url);
    }
}
