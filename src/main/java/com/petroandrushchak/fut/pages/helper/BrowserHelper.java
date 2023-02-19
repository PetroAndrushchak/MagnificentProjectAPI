package com.petroandrushchak.fut.pages.helper;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.petroandrushchak.fut.actions.WebBrowserAction;
import com.petroandrushchak.fut.actions.WebBrowserCondition;
import com.petroandrushchak.fut.pages.webdriver.WebDriverManager;
import com.petroandrushchak.helper.Waiter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.json.JsonException;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.Condition.not;
import static com.petroandrushchak.MagnificentProjectApiApplication.browserConfigs;

@Slf4j
@UtilityClass
public class BrowserHelper {

    public static void reloadThePage() {
        WebDriver driver = WebDriverManager.getDriver();
        driver.navigate().refresh();
    }

    public static void doActionUntilConditionWithNoWait(WebBrowserAction actionTodo, WebBrowserCondition untilCondition) {
        doActionUntilCondition(actionTodo, untilCondition, Duration.ofMillis(100));
    }

    public static void doActionUntilCondition(WebBrowserAction actionTodo, WebBrowserCondition untilCondition) {
        doActionUntilCondition(actionTodo, untilCondition, Duration.ofSeconds(1));
    }

    public static void doActionUntilCondition(WebBrowserAction actionTodo, WebBrowserCondition untilCondition, Duration timeToSleep) {
        for (int i = 1; i <= 10; i++) {
            log.info("Do action until condition ... try number " + i);
            actionTodo.doingSomethingOnWebPage();
            Waiter.waitFor(timeToSleep);
            log.info("Checking is condition is present ... ");
            if (!untilCondition.isConditionPresent()) {
                log.info("Condition is not present.. waiting for one second ... ");
            } else {
                log.info("Condition is present, finishing doing actions ");
                return;
            }
        }
        throw new RuntimeException("do action until condition failed !!!");
    }

    public static void doActionUntilConditionIgnoringElementNotFound(WebBrowserAction actionTodo, WebBrowserCondition untilCondition) {
        for (int i = 1; i <= 5; i++) {
            log.info("Do action until condition ... try number " + i);
            try {
                actionTodo.doingSomethingOnWebPage();
            } catch (JsonException | TimeoutException | StaleElementReferenceException e) {
                log.error("Timeout waiting for element. continue to check if condition is met");
            }
            Waiter.waitFor(Duration.ofMillis(100));
            log.info("Checking is condition is present ... ");
            if (!untilCondition.isConditionPresent()) {
                log.info("Condition is not present.. waiting for one second ... ");
            } else {
                log.info("Condition is present, finishing doing actions ");
                return;
            }
        }
        throw new RuntimeException("do action until condition failed !!!");
    }


    public static void clickOnButtonUntilItBecomeInvisible(SelenideElement button) {
        log.info("Clicking on the button until it become invisible");
        button.shouldBe(Condition.visible);
        Awaitility.await().pollInSameThread()
                  .atMost(browserConfigs().implicitWait())
                  .pollInterval(Duration.ofSeconds(1))
                  .ignoreException(ElementNotFound.class)
                  .until(() -> {
                      if (button.is(not(Condition.visible))) return true;
                      button.click();
                      return button.is(not(Condition.visible));
                  });

    }

    public static void clickByActionWithOutWaiting(SelenideElement element) {
        log.info("Clicking on element by JS without waiting");
        element.click(ClickOptions.usingJavaScript());
        try {
            clickByAction(element);
        } catch (NoSuchElementException | ElementClickInterceptedException | JsonException e) {
            log.error("Failed to click by action on the element");
        }
    }

    public static void clickByAction(WebElement element) {
        Actions actions = new Actions(WebDriverManager.getDriver());
        log.debug("Clicking by action on element... ");
        actions.moveToElement(element).click().build().perform();
        log.debug("Clicking by action on element performed!");
    }

    public static void deleteAllCookiesFromBrowser() {
        WebDriverManager.getDriver().manage().deleteAllCookies();
    }

    public static void saveCookiesInBrowser(List<Cookie> cookies) {
        cookies.forEach(cookie -> {
            WebDriverManager.getDriver().manage().addCookie(cookie);
        });
    }

    public static Set<Cookie> readAllCookiesFromBrowser() {
        return WebDriverManager.getDriver().manage().getCookies();
    }

}
