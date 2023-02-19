package com.petroandrushchak.fut.steps;

import com.petroandrushchak.aop.CloseBrowser;
import com.petroandrushchak.fut.pages.helper.Page;
import com.petroandrushchak.model.fut.SnippingModel;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class FUTWebAppSteps {

    @Autowired LogInSteps logInSteps;
    @Autowired FUTWebAppNavigationSteps navigationSteps;
    @Autowired FUTSnippingSteps snippingSteps;

    @CloseBrowser
    public void performSnipping(FutEaAccountView futEaAccountUiModel, SnippingModel snippingModel) {
        logInSteps.logInIntoFUTWebApp(futEaAccountUiModel);
        navigationSteps.navigateToPage(Page.SEARCH_TRANSFER_MARKET_PAGE);
        snippingSteps.performSnipping(snippingModel);
        //TODO Snipping

    }


}
