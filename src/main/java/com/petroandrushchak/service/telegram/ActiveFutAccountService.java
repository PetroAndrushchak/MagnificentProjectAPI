package com.petroandrushchak.service.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ActiveFutAccountService {

  //  private final ActiveFutAccountRepository activeFutAccountRepository;

//    public ActiveFutAccountService(ActiveFutAccountRepository activeFutAccountRepository) {
//        this.activeFutAccountRepository = activeFutAccountRepository;
//    }
//
//    public boolean isActiveFutAccountSet() {
//        log.info("Checking if active FUT account is set");
//        var result = activeFutAccountRepository.findAll();
//        var isActiveFutAccountSet = !result.isEmpty();
//        log.info("Active FUT account is set: " + isActiveFutAccountSet);
//        return isActiveFutAccountSet;
//    }
//
//    public String getActiveFutAccountName() {
//        log.info("Getting active FUT account name");
//        var result = activeFutAccountRepository.findAll();
//        var activeFutAccountName = result.get(0).getFutAccount().getUsername();
//        log.info("Active FUT account name is: " + activeFutAccountName);
//        return activeFutAccountName;
//    }
}
