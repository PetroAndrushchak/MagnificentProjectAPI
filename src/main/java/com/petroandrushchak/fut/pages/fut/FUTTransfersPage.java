package com.petroandrushchak.fut.pages.fut;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.fut.pages.BasePage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.codeborne.selenide.Selenide.$;

@Slf4j
@Component
public class FUTTransfersPage extends BasePage {

    SelenideElement searchTheTransferMarketButton = $(".TransfersHub .ut-tile-transfer-market header");

    public void clickOnTheSearchTransferMarketBlock() {
        searchTheTransferMarketButton.click();
    }

    @Override
    public boolean isOpened() {
        return $(".TransfersHub").is(Condition.visible);
    }
}
