package com.petroandrushchak.fut.pages.fut;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.fut.pages.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
@Component
public class FUTAppMainPage extends BasePage<FUTAppMainPage> {

    SelenideElement clubInfoLabel = $(".ut-navigation-container-view .view-navbar-clubinfo");

    @Override
    public boolean isOpened() {
        return clubInfoLabel.is(Condition.visible);
    }

    @Override
    public FUTAppMainPage waitUntilLoaded() {
        clubInfoLabel.shouldBe(Condition.visible);
        return this;
    }
}
