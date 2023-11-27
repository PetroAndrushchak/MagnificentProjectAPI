package com.petroandrushchak.service.firebase;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.petroandrushchak.entity.firebase.MailEntityFirebase;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MailServiceFirebase {

    private static final String MAIL_ENTITY_COLLECTION = "Mail_Accounts";

    @Autowired
    private Firestore firestore;

    @SneakyThrows
    String createIfNotExistMailAccount(MailEntityFirebase mailEntity) {
        Optional<QueryDocumentSnapshot> mailAccountDocumentId = firestore.collection(MAIL_ENTITY_COLLECTION)
                                                                         .whereEqualTo("emailAddress", mailEntity.getEmailAddress())
                                                                         .get()
                                                                         .get()
                                                                         .getDocuments()
                                                                         .stream().findFirst();
        if (mailAccountDocumentId.isPresent()) {
            return mailAccountDocumentId.get().getId();
        } else {
            DocumentReference documentReference = firestore.collection(MAIL_ENTITY_COLLECTION).add(mailEntity).get();
            return documentReference.getId();
        }
    }


}
