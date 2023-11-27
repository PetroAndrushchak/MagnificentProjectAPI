package com.petroandrushchak.service.firebase;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.petroandrushchak.entity.firebase.FutAccountEntityFirebase;
import com.petroandrushchak.entity.firebase.MailEntityFirebase;
import com.petroandrushchak.view.FutEaAccountView;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FutAccountServiceFirebase {

    @Autowired MailServiceFirebase mailServiceFirebase;

    private static final String FUT_ACCOUNTS_COLLECTION = "FUT_Accounts";

    @Autowired
    private Firestore firestore;

    @SneakyThrows
    public FutEaAccountView getFutAccountById(String id) {
        DocumentSnapshot futAccountEntityFirebase = firestore.collection(FUT_ACCOUNTS_COLLECTION)
                                                             .document(id)
                                                             .get()
                                                             .get();
        return getFutAccountByDocumentId(futAccountEntityFirebase);
    }

    @SneakyThrows
    public List<FutEaAccountView> findAllFutAccounts() {
        return firestore.collection(FUT_ACCOUNTS_COLLECTION).get().get().getDocuments()
                        .stream()
                        .map(this::getFutAccountByDocumentId)
                        .toList();
    }

    @SneakyThrows
    private FutEaAccountView getFutAccountByDocumentId(DocumentSnapshot documentSnapshot) {
        FutAccountEntityFirebase futAccountEntityFirebase = documentSnapshot.toObject(FutAccountEntityFirebase.class);
        MailEntityFirebase mailEntityFirebase = firestore.collection("Mail_Accounts")
                                                         .document(futAccountEntityFirebase.getMailDocumentId())
                                                         .get()
                                                         .get()
                                                         .toObject(MailEntityFirebase.class);

        return FutEaAccountView.anFutEaAccount()
                               .withId(documentSnapshot.getId())
                               .withUsername(futAccountEntityFirebase.getUsername())
                               .withEaLogin(futAccountEntityFirebase.getEaLogin())
                               .withEaPassword(futAccountEntityFirebase.getEaPassword())
                               .withEaEmailEmail(mailEntityFirebase.getEmailAddress())
                               .withEaEmailPassword(mailEntityFirebase.getEmailPassword())
                               .build();
    }

    public void createFutAccount(FutEaAccountView futEaAccountView) {
        var mailEntity = new MailEntityFirebase();
        mailEntity.setEmailAddress(futEaAccountView.getEaEmailEmail());
        mailEntity.setEmailPassword(futEaAccountView.getEaEmailPassword());

        String mailDocumentId = mailServiceFirebase.createIfNotExistMailAccount(mailEntity);

        var futAccountEntity = new FutAccountEntityFirebase();
        futAccountEntity.setUsername(futEaAccountView.getUsername());
        futAccountEntity.setEaLogin(futEaAccountView.getEaLogin());
        futAccountEntity.setEaPassword(futEaAccountView.getEaPassword());
        futAccountEntity.setMailDocumentId(mailDocumentId);

        firestore.collection(FUT_ACCOUNTS_COLLECTION).add(futAccountEntity);

    }

}
