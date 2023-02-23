package com.petroandrushchak.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "browser_process")
@Data
public class BrowserProcessEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fut_account_id", referencedColumnName = "id")
    @JoinColumn(name = "ea_login", referencedColumnName = "ea_login")
    FutAccountEntity futAccount;

    @Enumerated(EnumType.STRING)
    Status status;

    public static BrowserProcessEntity of(FutAccountEntity futAccount, Status status) {
        var browserProcessEntity = new BrowserProcessEntity();
        browserProcessEntity.setFutAccount(futAccount);
        browserProcessEntity.setStatus(status);
        return browserProcessEntity;
    }



}
