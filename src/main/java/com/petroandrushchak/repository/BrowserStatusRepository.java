package com.petroandrushchak.repository;

import com.petroandrushchak.entity.BrowserProcessEntity;
import com.petroandrushchak.entity.FutAccountEntity;
import com.petroandrushchak.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrowserStatusRepository extends JpaRepository<BrowserProcessEntity, Long> {

   List<BrowserProcessEntity> findByFutAccountAndStatus(FutAccountEntity futAccountEntity, Status status);

   List<BrowserProcessEntity> findByStatus(Status status);

}
