package com.petroandrushchak.futbin.pages.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Component
public class PrivacyModal {

    SelenideElement agreeButton = $("div[class *='footer'] button[mode = 'primary']");

    @RealPerson
    public void agree() {
        agreeButton.click();
    }

    public boolean isDisplayed() {
        return agreeButton.is(Condition.visible);
    }

    @RealPerson
    public void shouldNotBeDisplayed() {
        agreeButton.shouldNotBe(Condition.visible);
    }
}
