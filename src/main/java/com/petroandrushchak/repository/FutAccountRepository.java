package com.petroandrushchak.repository;

import com.petroandrushchak.entity.FutAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FutAccountRepository extends JpaRepository<FutAccountEntity, Long> {

    @Query(value = "SELECT account FROM FutAccountEntity account " +
            "where lower(account.username) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(account.eaLogin) like lower(concat('%', :searchTerm, '%')) "
//            "or lower(account.eaEmail) like lower(concat('%', :searchTerm, '%'))"
    )
    List<FutAccountEntity> search(@Param("searchTerm") String searchTerm);

}