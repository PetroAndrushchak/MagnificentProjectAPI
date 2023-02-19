package com.petroandrushchak.fut.pages.fut;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.fut.pages.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;
import static com.petroandrushchak.fut.pages.helper.BrowserHelper.clickOnButtonUntilItBecomeInvisible;

@Slf4j
@Component
public class FUTAppLogInPage extends BasePage<FUTAppLogInPage> {

    private static final String PAGE_URL = "https://www.ea.com/fifa/ultimate-team/web-app/";

    SelenideElement logInButton = $(".call-to-action");

    public void openFUTLogInPage() {
        log.info("Opening Web App log in page ... ");
        Selenide.clearBrowserCookies();
        Selenide.open(PAGE_URL);
        logInButton.shouldBe(Condition.visible)
                   .shouldBe(Condition.interactable);
    }

    @RealPerson
    public void clickLogInButton() {
        log.info("Clicking Log in Button");
        clickOnButtonUntilItBecomeInvisible(logInButton);
    }

    @Override
    public boolean isOpened() {
        return logInButton.is(Condition.visible);
    }

    @Override
    public FUTAppLogInPage waitUntilLoaded() {
        logInButton.shouldBe(Condition.visible);
        return this;
    }
}
