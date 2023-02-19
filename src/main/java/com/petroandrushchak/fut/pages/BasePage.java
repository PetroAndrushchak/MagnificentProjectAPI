package com.petroandrushchak.fut.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.fut.pages.components.FutLeftControlPanel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
public abstract class BasePage<T> {

    private FutLeftControlPanel leftControlPanel;

    SelenideElement fifaHeader = $(".ut-fifa-header-view");

    @Autowired
    public final void setLeftControlPanel(FutLeftControlPanel leftControlPanel) {
        this.leftControlPanel = leftControlPanel;
    }

    public FutLeftControlPanel leftControlPanel() {
        return leftControlPanel;
    }

    public abstract boolean isOpened();

    public T waitUntilLoaded() {
        log.warn("Waiting for page to load ... BASE PAGE, for correct page load please override this method");
        fifaHeader.shouldBe(Condition.visible);
        return (T) this;
    }

}
