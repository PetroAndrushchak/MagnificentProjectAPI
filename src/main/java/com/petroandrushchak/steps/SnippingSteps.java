package com.petroandrushchak.steps;

import com.petroandrushchak.entity.BrowserProcessEntity;
import com.petroandrushchak.exceptions.FutEaAccountNotFound;
import com.petroandrushchak.fut.steps.FUTWebAppSteps;
import com.petroandrushchak.repository.FutAccountRepository;
import com.petroandrushchak.repository.BrowserStatusRepository;
import com.petroandrushchak.view.BrowserProcessView;
import com.petroandrushchak.view.request.SnippingRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.petroandrushchak.entity.Status.IN_PROGRESS;

@Component
public class SnippingSteps {

    @Autowired BrowserStatusRepository browserStatusRepository;
    @Autowired FutAccountRepository futAccountRepository;

    @Autowired FUTWebAppSteps futWebAppSteps;

    public BrowserProcessView startSnipping(SnippingRequestBody snippingRequestBody) {

        var futAccount = futAccountRepository.findById(snippingRequestBody.getFutEaAccountId());
        if (futAccount.isEmpty()) throw new FutEaAccountNotFound(snippingRequestBody.getFutEaAccountId());

        var browserProcessEntity = BrowserProcessEntity.of(futAccount.get(), IN_PROGRESS);
        var newSnippingEntity = browserStatusRepository.save(browserProcessEntity);

        // Start snipping process
        futWebAppSteps.performSnipping(newSnippingEntity.getId());

        return BrowserProcessView.builder()
                                 .status(IN_PROGRESS)
                                 .id(newSnippingEntity.getId())
                                 .futEaAccountId(snippingRequestBody.getFutEaAccountId())
                                 .playerItem(snippingRequestBody.getPlayerItem()).build();
    }
}
