package com.petroandrushchak.repository;

import com.petroandrushchak.entity.BrowserProcessEntity;
import com.petroandrushchak.entity.Status;

import java.util.List;

public interface BrowserStatusRepository {

   //List<BrowserProcessEntity> findByFutAccountAndStatus(FutAccountEntity futAccountEntity, Status status);

   List<BrowserProcessEntity> findByStatus(Status status);

}
