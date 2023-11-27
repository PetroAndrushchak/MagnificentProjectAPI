package com.petroandrushchak.steps;

import com.petroandrushchak.entity.BrowserProcessEntity;
import com.petroandrushchak.fut.steps.FUTWebAppSteps;
import com.petroandrushchak.model.fut.Item;
import com.petroandrushchak.service.firebase.FutAccountServiceFirebase;
import com.petroandrushchak.view.BrowserProcessView;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.petroandrushchak.entity.Status.IN_PROGRESS;

@Slf4j
@Component
public class SnippingSteps {

    @Autowired FutAccountServiceFirebase futAccountServiceFirebase;

    @Autowired FUTWebAppSteps futWebAppSteps;

    //TODO Change SnippingRequestBody to View
    public BrowserProcessView startSnipping(FutEaAccountView futEaAccount, Item item) {

        var futAccount = futAccountServiceFirebase.getFutAccountById(futEaAccount.getId());

        var browserProcessEntity = BrowserProcessEntity.of(futAccount.getId(), IN_PROGRESS);
       // var newSnippingEntity = browserStatusRepository.save(browserProcessEntity);

        // Start snipping process
      //  futWebAppSteps.performSnipping(newSnippingEntity.getId());

//        return BrowserProcessView.builder()
//                                 .status(IN_PROGRESS)
//                                 .id(newSnippingEntity.getId())
//                                 .futEaAccountId(futEaAccount.getId())
//                                 .item(item).build();
        return null;
    }
}
