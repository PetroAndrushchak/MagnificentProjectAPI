package com.petroandrushchak.service;


import com.petroandrushchak.entity.BrowserProcessEntity;
import com.petroandrushchak.entity.Status;
import com.petroandrushchak.repository.BrowserStatusRepository;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BrowserProcessService {

    private final BrowserStatusRepository browserStatusRepository = null;


    public void completeBrowserProcess(Long snippingId) {
        log.info("Completing Snipping Process for Snipping Id: " + snippingId);
        var snippingEntity = getBrowserProcessEntity(snippingId);
        snippingEntity.setStatus(Status.COMPLETED);
      //  browserStatusRepository.save(snippingEntity);
    }

    public void cancelBrowserProcess(Long browserProcess) {
        log.info("Canceling Browser Process for Browser Process Id: " + browserProcess);
        var browserProcessEntity = getBrowserProcessEntity(browserProcess);
        browserProcessEntity.setStatus(Status.CANCELLED);
     //   browserStatusRepository.save(browserProcessEntity);
    }

    public List<BrowserProcessEntity> getBrowserProcessEntitiesForFutAccount(FutEaAccountView futEaAccountView, Status status) {
        log.info("Getting Browser Process Entities for FUT Account: " + futEaAccountView);
//        var futAccountEntity = FutEaAccountMapper.INSTANCE.accountModelToEntity(futEaAccountView);
//        return browserStatusRepository.findByFutAccountAndStatus(futAccountEntity, Status.IN_PROGRESS);
        return null;
    }

    public void resetBrowserProcessForFutAccount(FutEaAccountView futEaAccountView) {
        log.info("Resetting Browser Process for FUT Account: " + futEaAccountView);
//        var futAccountEntity = FutEaAccountMapper.INSTANCE.accountModelToEntity(futEaAccountView);
//        var browserProcessEntities = browserStatusRepository.findByFutAccountAndStatus(futAccountEntity, Status.IN_PROGRESS);
//        if (browserProcessEntities.isEmpty()) return;
//
//        browserProcessEntities.forEach(browserProcessEntity -> {
//            browserProcessEntity.setStatus(Status.RESET);
//        //    browserStatusRepository.save(browserProcessEntity);
//        });
    }

    public BrowserProcessEntity getBrowserProcessEntity(Long snippingId) {
        log.info("Getting Snipping Entity for Snipping Id: " + snippingId);

//        var snippingEntity = browserStatusRepository.findById(snippingId);
//
//        if (snippingEntity.isEmpty()) throw new BrowserProcessNotFound(snippingId);
//        return snippingEntity.get();
        return null;
    }

    public boolean isAnyBrowserProcessRunningForFutAccount(FutEaAccountView futEaAccountView) {
        log.info("Checking if any browser process is running for FUT Account: " + futEaAccountView);
//        var futAccountEntity = FutEaAccountMapper.INSTANCE.accountModelToEntity(futEaAccountView);
//        var result = browserStatusRepository.findByFutAccountAndStatus(futAccountEntity, Status.IN_PROGRESS);
//        log.info("Result: " + result);
//        return !result.isEmpty();
        return false;
    }
}
