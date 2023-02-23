package com.petroandrushchak.service;

import com.petroandrushchak.exceptions.FutEaAccountNotFound;
import com.petroandrushchak.mapper.FutEaAccountMapper;
import com.petroandrushchak.repository.FutAccountRepository;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class FutAccountService {

    private final FutAccountRepository futAccountRepository;

    public FutAccountService(FutAccountRepository futAccountRepository) {
        this.futAccountRepository = futAccountRepository;
    }

    public FutEaAccountView getFutAccountById(Long id) {
        log.info("Searching Fut account by id: " + id);
        var optionalResult = futAccountRepository.findById(id);
        if (optionalResult.isEmpty()) {
            log.info("Error finding FUT Account by id: " + id);
            throw new FutEaAccountNotFound(id);
        } else {
            return FutEaAccountMapper.INSTANCE.entityAccountToModel(optionalResult.get());
        }

    }

    public List<FutEaAccountView> findFutAccounts(String stringFilter) {
        log.info("Searching Fut accounts with filter value: " + stringFilter);
        var result = futAccountRepository.search(stringFilter)
                                         .stream()
                                         .map(FutEaAccountMapper.INSTANCE::entityAccountToModel)
                                         .toList();
        log.info("Search result is: " + result.size());
        return result;
    }

    public List<FutEaAccountView> findAllFutAccounts() {
        var futAccountsEntities = futAccountRepository.findAll();
        return futAccountsEntities.stream()
                                  .map(FutEaAccountMapper.INSTANCE::entityAccountToModel)
                                  .toList();
    }

    public void saveFutAccount(FutEaAccountView futEaAccountUiModel) {
        log.info("Saving Fut Account to DB: " + futEaAccountUiModel);
        futAccountRepository.save(FutEaAccountMapper.INSTANCE.accountModelToEntity(futEaAccountUiModel));
    }

    public void deleteFutAccount(FutEaAccountView futEaAccountUiModel) {
        log.info("Deleting Fut Account from DB:" + futEaAccountUiModel);
        futAccountRepository.delete(FutEaAccountMapper.INSTANCE.accountModelToEntity(futEaAccountUiModel));
    }
}
