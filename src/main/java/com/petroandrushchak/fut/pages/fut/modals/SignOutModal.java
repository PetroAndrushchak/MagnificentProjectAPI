package com.petroandrushchak.fut.pages.fut.modals;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
@Component
public class SignOutModal {

    SelenideElement signOutButton = $(".ea-dialog-view-type--message .ut-button-group button:nth-of-type(1)");

    @RealPerson
    public void clickSingOutButton() {
        log.info("Clicking Sign Out Button");
        signOutButton.click();
    }

    public boolean isSignOutModalDisplayed() {
        log.info("Checking if Sign Out Modal is displayed");
        return signOutButton.is(Condition.visible);
    }
}
