package com.petroandrushchak.fut.pages.fut;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.fut.pages.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
@Component
public class FUTSettingsPage extends BasePage {

    SelenideElement userDataContainer = $(".user-info .user-data-container");

    SelenideElement singOutButton = $(".ut-app-settings-actions .ut-button-group:nth-of-type(1) button:nth-of-type(2)");

    @RealPerson
    public FUTSettingsPage clickOnSingOutButton() {
        log.info("Clicking on the Sing Out button");
        singOutButton.click();
        return this;
    }

    @Override
    public boolean isOpened() {
        log.info("Checking if the Settings page is opened");
        return userDataContainer.is(Condition.visible);
    }
}
