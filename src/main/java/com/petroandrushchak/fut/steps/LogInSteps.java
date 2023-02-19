package com.petroandrushchak.fut.steps;

import com.petroandrushchak.entity.mongo.FutWebCookiesItem;
import com.petroandrushchak.fut.FUTWebAppPageHelper;
import com.petroandrushchak.fut.pages.ea.EASecurityCodePage;
import com.petroandrushchak.fut.pages.ea.EASignInPage;
import com.petroandrushchak.fut.pages.ea.EASignInVerificationPage;
import com.petroandrushchak.fut.pages.fut.FUTAppLogInPage;
import com.petroandrushchak.fut.pages.fut.FUTAppMainPage;
import com.petroandrushchak.fut.pages.fut.modals.messages.ContinueMessageModal;
import com.petroandrushchak.fut.pages.helper.Page;
import com.petroandrushchak.helper.Waiter;
import com.petroandrushchak.repository.mongo.FutWebCookiesItemRepository;
import com.petroandrushchak.service.email.EmailService;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
public class LogInSteps {

    @Autowired FUTAppLogInPage futAppLogInPage;
    @Autowired FUTAppMainPage mainPage;

    @Autowired EASignInPage eaSignInPage;
    @Autowired EASignInVerificationPage eaSignInVerificationPage;
    @Autowired EASecurityCodePage eaSecurityCodePage;

    @Autowired ContinueMessageModal continueMessageModal;

    @Autowired FUTWebAppNavigationSteps navigationSteps;
    @Autowired BrowserSteps browserSteps;

    @Autowired FUTWebAppPageHelper futWebAppPageHelper;

    @Autowired FutWebCookiesItemRepository futWebCookiesItemRepository;

    public void logInIntoFUTWebApp(FutEaAccountView futEaAccountUiModel) {
        futAppLogInPage.openFUTLogInPage();
        Waiter.waitFor(Duration.ofSeconds(10));
        browserSteps.reloadPage();

        futAppLogInPage.clickLogInButton();

        Page currentlyOpenedPage = futWebAppPageHelper.getCurrentlyOpenedPage();

        if (currentlyOpenedPage == Page.EA_SIGN_IN_PAGE) {
            logInViaEA(futEaAccountUiModel);
        } else if (currentlyOpenedPage == Page.MAIN_PAGE) {
            log.info("Successfully logged in into Fut  Web App ");
            closeAllMessagesFromEAIfOpened();
        } else {
            throw new RuntimeException("Currently Opened page is not handled by code, page: " + currentlyOpenedPage);
        }
    }

    private void logInViaEA(FutEaAccountView futEaAccountUiModel) {

        saveCookiesForAccountIntoBrowserIfExist(futEaAccountUiModel.getEaEmailEmail());
        boolean shouldEACookiesBeCached = false;

        eaSignInPage.logIn(futEaAccountUiModel.getEaLogin(), futEaAccountUiModel.getEaPassword());
        if (eaSignInPage.isCurrentlyOpened()) {
            eaSignInPage.logIn(futEaAccountUiModel.getEaLogin(), futEaAccountUiModel.getEaPassword());
        }

        Page currentlyOpenedPage = futWebAppPageHelper.getCurrentlyOpenedPage();
        if (currentlyOpenedPage == Page.EA_SIGN_IN_VERIFICATION_PAGE) {
            LocalDateTime timeVerificationCodeSent = eaSignInVerificationPage.clickSendSecurityCode();
            String code = EmailService.forUser(futEaAccountUiModel.getEaEmailEmail(), futEaAccountUiModel.getEaEmailPassword())
                                      .getEASecurityCode(timeVerificationCodeSent);
            eaSecurityCodePage.logIn(code);
            shouldEACookiesBeCached = true;
        }

        currentlyOpenedPage = futWebAppPageHelper.getCurrentlyOpenedPage();

        if (currentlyOpenedPage == Page.EA_SIGN_IN_VERIFICATION_PAGE) {
            log.info("EA Sign In Verification Page is still opened");
            throw new RuntimeException("EA Sign In Verification Page is still opened");
        } else if (currentlyOpenedPage == Page.MAIN_PAGE) {
            log.info("Successfully logged in into Fut Web App ");
            closeAllMessagesFromEAIfOpened();

            //TODO Save FUT Web Local Storage

            if (shouldEACookiesBeCached) {
                log.info("Caching EA cookies for account: " + futEaAccountUiModel.getEaEmailEmail());

                navigationSteps.logOut();
                futAppLogInPage.waitUntilLoaded()
                               .clickLogInButton();
                eaSignInPage.waitUntilLoaded();

                saveCookiesForAccountFromBrowserToDb(futEaAccountUiModel.getEaEmailEmail());

                eaSignInPage.logIn(futEaAccountUiModel.getEaLogin(), futEaAccountUiModel.getEaPassword());
                mainPage.waitUntilLoaded();
            } else {
                log.info("Cookies for account {} are already cached, so do not need to do it", futEaAccountUiModel.getEaEmailEmail());
            }

        } else {
            throw new RuntimeException("Currently Opened page is not handled by code, page: " + currentlyOpenedPage);
        }
    }

    private void closeAllMessagesFromEAIfOpened() {
        var isNoMessageModalDisplayed = Waiter.isConditionTrueAtMostDuringPeriod(Duration.ofSeconds(5), () -> !continueMessageModal.isContinueMessageModalDisplayed());
        if (!isNoMessageModalDisplayed) {
            continueMessageModal.clickContinueButton();
        }
        continueMessageModal.modalShouldBeNotDisplayed();
    }

    private void saveCookiesForAccountIntoBrowserIfExist(String email) {
        log.info("Trying to find cookies for account: " + email);
        var arePresent = futWebCookiesItemRepository.areCookiesExistForEAFutAccount(email);
        if (arePresent) {
            var cookies = futWebCookiesItemRepository.findItemByEaFutEmailAddress(email).getCookieDto();
            browserSteps.saveCookiesInBrowser(cookies);
        } else {
            log.info("Cookies for account: " + email + " are not found");
        }
    }

    private void saveCookiesForAccountFromBrowserToDb(String email) {
        log.info("Trying to save cookies for account: " + email);
        var cookies = browserSteps.readCookiesFromBrowser();

        FutWebCookiesItem futWebCookiesItem = new FutWebCookiesItem();
        futWebCookiesItem.setEaFutEmail(email);
        futWebCookiesItem.setCookieDto(cookies);
        futWebCookiesItem.setDateTimeAdded(LocalDateTime.now());

        futWebCookiesItemRepository.saveItem(futWebCookiesItem);
    }


}
