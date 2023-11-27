package com.petroandrushchak.firebase;

import com.petroandrushchak.service.firebase.FutAccountServiceFirebase;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class FirebaseConnectTest {

    @Autowired FutAccountServiceFirebase futAccountServiceFirebase;

    @SneakyThrows
    @Test
    void test() {

        FutEaAccountView futAccountEntity = FutEaAccountView.anFutEaAccount()
                                                            .withUsername("petroandrushchak")
                                                            .withEaLogin("petroandrushchak")
                                                            .withEaPassword("petroandrushchak")
                                                            .withEaEmailEmail("testMail")
                                                            .withEaEmailPassword("testPass")
                                                            .build();

        var futAccounts = futAccountServiceFirebase.findAllFutAccounts();
        System.out.println("dsfsdf");

    }
}
