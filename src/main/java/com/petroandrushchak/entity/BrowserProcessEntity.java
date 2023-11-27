package com.petroandrushchak.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class BrowserProcessEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    Long id;

    String futAccountId;

    @Enumerated(EnumType.STRING)
    Status status;

    public static BrowserProcessEntity of(String futAccountId, Status status) {
        var browserProcessEntity = new BrowserProcessEntity();
        browserProcessEntity.setFutAccountId(futAccountId);
        browserProcessEntity.setStatus(status);
        return browserProcessEntity;
    }



}
