package com.petroandrushchak.fut.pages.ea;

import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
@Component
public class EASecurityCodePage {

    SelenideElement securityCodeInputField = $("#twoFactorCode");
    SelenideElement logInButton = $("#btnSubmit");

    @RealPerson
    public void logIn(String securityCode) {
        securityCodeInputField.sendKeys(securityCode);
        logInButton.click();
    }
}
