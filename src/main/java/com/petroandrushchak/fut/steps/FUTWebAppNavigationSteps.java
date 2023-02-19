package com.petroandrushchak.fut.steps;

import com.petroandrushchak.fut.exeptions.NotFoundSwitchException;
import com.petroandrushchak.fut.pages.fut.FUTAppMainPage;
import com.petroandrushchak.fut.pages.fut.FUTSearchTransferMarketPage;
import com.petroandrushchak.fut.pages.fut.FUTSettingsPage;
import com.petroandrushchak.fut.pages.fut.FUTTransfersPage;
import com.petroandrushchak.fut.pages.fut.modals.SignOutModal;
import com.petroandrushchak.fut.pages.helper.BrowserHelper;
import com.petroandrushchak.fut.pages.helper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FUTWebAppNavigationSteps {

    @Autowired FUTAppMainPage mainPage;
    @Autowired FUTSettingsPage settingsPage;

    @Autowired SignOutModal signOutModal;

    @Autowired FUTTransfersPage transfersPage;
    @Autowired FUTSearchTransferMarketPage transferMarketSearchPage;

    public void navigateToPage(Page page) {
        log.info("Navigate to page: " + page);

        switch (page) {
            case SEARCH_TRANSFER_MARKET_PAGE -> {
                BrowserHelper.doActionUntilCondition(mainPage.leftControlPanel()::clickOnTheTransfersLabel, transfersPage::isOpened);
                BrowserHelper.doActionUntilCondition(transfersPage::clickOnTheSearchTransferMarketBlock, transferMarketSearchPage::isOpened);
            }
            default -> throw new NotFoundSwitchException("Page is not defined");
        }
    }

    public void logOut() {
        mainPage.leftControlPanel().clickOnTheSettingsLabel();
        BrowserHelper.doActionUntilCondition(settingsPage::clickOnSingOutButton, signOutModal::isSignOutModalDisplayed);
        signOutModal.clickSingOutButton();
    }

}
