package com.petroandrushchak.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "fut_account")
public class FutAccountEntity {

    @Id
    @GeneratedValue
    Long id;

    @Column(name = "username", nullable = false)
    String username;

    @Column(name = "ea_login", nullable = false)
    String eaLogin;

    @Column(name = "ea_password", nullable = false)
    String eaPassword;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email_id", referencedColumnName = "id")
    MailEntity eaEmail;

}
