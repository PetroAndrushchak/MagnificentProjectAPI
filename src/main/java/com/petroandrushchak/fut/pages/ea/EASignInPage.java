package com.petroandrushchak.fut.pages.ea;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
@Component
public class EASignInPage {

    SelenideElement emailAddressInoutField = $("#email");
    SelenideElement passwordInputField = $("#password");
    SelenideElement signInButton = $("#logInBtn");

    @RealPerson
    public void logIn(String email, String password) {
        emailAddressInoutField.clear();
        emailAddressInoutField.sendKeys(email);

        passwordInputField.sendKeys(password);

        signInButton.click();
    }

    public boolean isCurrentlyOpened() {
        return emailAddressInoutField.is(Condition.visible) && passwordInputField.is(Condition.visible);
    }

    public void waitUntilLoaded() {
        emailAddressInoutField.shouldBe(Condition.visible);
    }

}
