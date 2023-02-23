package com.petroandrushchak.fut.steps;

import com.petroandrushchak.aop.CloseBrowser;
import com.petroandrushchak.fut.pages.helper.Page;
import com.petroandrushchak.helper.Waiter;
import com.petroandrushchak.model.fut.SnippingModel;
import com.petroandrushchak.process.BrowserProcessHelper;
import com.petroandrushchak.service.BrowserProcessService;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Slf4j
@Component
public class FUTWebAppSteps {

    @Autowired LogInSteps logInSteps;
    @Autowired FUTWebAppNavigationSteps navigationSteps;
    @Autowired FUTSnippingSteps snippingSteps;

    @Autowired BrowserProcessService browserProcessService;

    @Autowired
    @Qualifier("taskExecutor")
    ThreadPoolTaskExecutor executor;

    @Autowired BrowserProcessHelper browserProcessHelper;

    @Async
    public void performSnipping(Long processID) {

        var completeFeature = executor.submitCompletable(() -> {
            log.info("Starting Snipping");
            /*
            //TODO Actual Snipping
             */
            Waiter.waitFor(Duration.ofMinutes(2));

            //Finish Action
            browserProcessService.completeBrowserProcess(processID);
        });

        browserProcessHelper.addTask(processID, completeFeature);
    }

    @CloseBrowser
    public void performSnipping(FutEaAccountView futEaAccountUiModel, SnippingModel snippingModel) {
        logInSteps.logInIntoFUTWebApp(futEaAccountUiModel);
        navigationSteps.navigateToPage(Page.SEARCH_TRANSFER_MARKET_PAGE);
        snippingSteps.performSnipping(snippingModel);
        //TODO Snipping
    }


}
