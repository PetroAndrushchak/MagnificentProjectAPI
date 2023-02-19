package com.petroandrushchak.fut.pages.components;

import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.fut.pages.helper.BrowserHelper;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Component
public class FutLeftControlPanel {

    SelenideElement homeButton = $(".icon-home");
    SelenideElement transfersButton = $(".icon-transfer");
    SelenideElement squadsButton = $("#.icon-squad");
    SelenideElement settingsButton = $(".icon-settings");

    @RealPerson
    public void clickOnTheSettingsLabel() {
        settingsButton.click();
    }

    @RealPerson
    public void clickOnTheTransfersLabel() {
        BrowserHelper.clickByActionWithOutWaiting(transfersButton);
    }

}
