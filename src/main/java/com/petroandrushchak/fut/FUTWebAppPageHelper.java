package com.petroandrushchak.fut;

import com.petroandrushchak.fut.exeptions.PageCanNotBeDetectedException;
import com.petroandrushchak.fut.pages.ea.EASignInPage;
import com.petroandrushchak.fut.pages.ea.EASignInVerificationPage;
import com.petroandrushchak.fut.pages.fut.FUTAppMainPage;
import com.petroandrushchak.fut.pages.helper.Page;
import com.petroandrushchak.helper.Waiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class FUTWebAppPageHelper {

    @Autowired EASignInVerificationPage eaSignInVerificationPage;
    @Autowired EASignInPage eaSignInPage;
    @Autowired FUTAppMainPage futAppMainPage;

    public Page getCurrentlyOpenedPage() {
        log.info("Currently opened page is: ");
        Optional<Page> page = Optional.empty();
        for (int i = 1; i <= 30; i++) {
            log.info("Checking currently opened page");
            if (eaSignInVerificationPage.isCurrentlyOpened()) {
                log.info("EA sign in verification code page ");
                page = Optional.of(Page.EA_SIGN_IN_VERIFICATION_PAGE);
            }
            if (eaSignInPage.isCurrentlyOpened()) {
                log.info("EA sign in  page ");
                page = Optional.of(Page.EA_SIGN_IN_PAGE);
            }
            if (futAppMainPage.isOpened()) {
                log.info("Web app main page");
                page = Optional.of(Page.MAIN_PAGE);
            }
            if (page.isPresent()) {
                break;
            }
            Waiter.waitForOneSecond();
        }

        if (page.isEmpty()) {
            throw new PageCanNotBeDetectedException();
        } else {
            return page.get();
        }


    }


}
