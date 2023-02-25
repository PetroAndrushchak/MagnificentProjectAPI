package com.petroandrushchak.fut.pages.webdriver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import static com.petroandrushchak.configs.ProjectConfigurations.browserConfigs;

@Slf4j
@UtilityClass
public class WebDriverManager {

    static {
        init();
    }

    public static WebDriver getDriver() {
        return WebDriverRunner.getWebDriver();
    }

    public static void closeWebDriver() {
        log.info("Closing WebDriver");
        if (WebDriverRunner.hasWebDriverStarted()) {

            try {
                WebDriverRunner.getWebDriver().quit();
            } catch (Exception e) {
                log.error("Error while closing WebDriver", e);
                WebDriverRunner.closeWebDriver();
            }

        } else {
            log.info("WebDriver is not started");
        }
    }

    public static void init() {

        Configuration.browser = browserConfigs().browser();
        Configuration.timeout = browserConfigs().implicitWait().toMillis();
        Configuration.headless = browserConfigs().headless();

    }

}
