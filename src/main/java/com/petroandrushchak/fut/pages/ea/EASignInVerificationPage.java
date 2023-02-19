package com.petroandrushchak.fut.pages.ea;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.RealPerson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
@Component
public class EASignInVerificationPage {

    SelenideElement sendSecurityCodeButton = $("#btnSendCode");

    @RealPerson
    public LocalDateTime clickSendSecurityCode() {
        var verificationCodeSentTime = LocalDateTime.now().minusMinutes(1);
        sendSecurityCodeButton.click();
        return verificationCodeSentTime;

    }

    public boolean isCurrentlyOpened() {
        return sendSecurityCodeButton.is(Condition.visible);
    }

}
