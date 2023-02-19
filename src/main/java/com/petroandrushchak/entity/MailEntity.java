package com.petroandrushchak.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@Table(name = "email")
public class MailEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    long id;

    @Column(name = "email", nullable = false)
    String emailAddress;

    @Column(name = "password", nullable = false)
    String emailPassword;

}
