package com.petroandrushchak.fut.pages.fut;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.petroandrushchak.aop.CloseBrowser;
import com.petroandrushchak.aop.RealPerson;
import com.petroandrushchak.fut.pages.BasePage;
import com.petroandrushchak.helper.Waiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static com.codeborne.selenide.Selenide.$;
import static com.petroandrushchak.fut.pages.helper.BrowserHelper.clickOnButtonUntilItBecomeInvisible;
import static org.awaitility.Awaitility.await;

@Slf4j
@Component
public class FUTAppLogInPage extends BasePage<FUTAppLogInPage> {

    private static final String PAGE_URL = "https://www.ea.com/fifa/ultimate-team/web-app/";
  // private static final String PAGE_URL = "https://www.google.com";

    SelenideElement logInButton = $(".call-to-action");

    //TODO Remove
   // @CloseBrowser
    public void openFUTLogInPage() {
        log.info("Opening Web App log in page ... ");
        Selenide.clearBrowserCookies();
        Selenide.open(PAGE_URL);

//    This code is needed for the case when you interrupt the snipping process
//        log.info("Waiting for 60 seconds ... ");
//        AtomicInteger i = new AtomicInteger();
//        await().atMost(Duration.ofSeconds(20))
//               .pollInSameThread()
//               .pollInterval(Duration.ofSeconds(5))
//               .until(() -> {
//                   if (Thread.currentThread().isInterrupted()) {
//                       log.info("Thread is interrupted ... ");
//                       return true;
//                   }
//                   log.info("Waiting for Log in button to be visible ... i = " + i.get());
//                   i.getAndIncrement();
//                   if (i.get() == 55) {
//                       return true;
//                   } else {
//                       return false;
//                   }
//               });
        log.info("Waiting for Log in button to be visible ... ");
        logInButton.shouldBe(Condition.visible)
                   .shouldBe(Condition.interactable);
    }

    @RealPerson
    public void clickLogInButton() {
        log.info("Clicking Log in Button");
        clickOnButtonUntilItBecomeInvisible(logInButton);
    }

    @Override
    public boolean isOpened() {
        return logInButton.is(Condition.visible);
    }

    @Override
    public FUTAppLogInPage waitUntilLoaded() {
        logInButton.shouldBe(Condition.visible);
        return this;
    }
}
