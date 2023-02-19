package com.petroandrushchak.fut.pages.fut.modals.messages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
@Component
public class ContinueMessageModal {

    SelenideElement modalContainer = $(".ut-livemessage");
    SelenideElement continueButton = $("button[class = 'btn-standard call-to-action']");
    SelenideElement liveMessageCounter = $("ut-livemessage-header--counter");

    public void clickContinueButton() {
        log.info("Clicking Continue Button");
        continueButton.click();
    }

    public boolean isContinueMessageModalDisplayed() {
        log.info("Checking if Continue Message Modal is displayed");
        return modalContainer.isDisplayed();
    }

    public void modalShouldBeNotDisplayed() {
        log.info("Checking if Continue Message Modal is not displayed");
        modalContainer.shouldBe(Condition.not(Condition.visible));
    }

}
