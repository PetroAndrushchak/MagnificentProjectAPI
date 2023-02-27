package com.petroandrushchak.steps;

import com.petroandrushchak.entity.BrowserProcessEntity;
import com.petroandrushchak.exceptions.FutEaAccountNotFound;
import com.petroandrushchak.fut.steps.FUTWebAppSteps;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.repository.FutAccountRepository;
import com.petroandrushchak.repository.BrowserStatusRepository;
import com.petroandrushchak.view.BrowserProcessView;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.petroandrushchak.entity.Status.IN_PROGRESS;

@Slf4j
@Component
public class SnippingSteps {

    @Autowired BrowserStatusRepository browserStatusRepository;
    @Autowired FutAccountRepository futAccountRepository;

    @Autowired FUTWebAppSteps futWebAppSteps;

    //TODO Change SnippingRequestBody to View
    public BrowserProcessView startSnipping(FutEaAccountView futEaAccount, Item item) {

        var futAccount = futAccountRepository.findById(futEaAccount.getId());
        if (futAccount.isEmpty()) throw new FutEaAccountNotFound(futEaAccount.getId());

        var browserProcessEntity = BrowserProcessEntity.of(futAccount.get(), IN_PROGRESS);
        var newSnippingEntity = browserStatusRepository.save(browserProcessEntity);

        // Start snipping process
        futWebAppSteps.performSnipping(newSnippingEntity.getId());

        return BrowserProcessView.builder()
                                 .status(IN_PROGRESS)
                                 .id(newSnippingEntity.getId())
                                 .futEaAccountId(futEaAccount.getId())
                                 .item(item).build();
    }
}
