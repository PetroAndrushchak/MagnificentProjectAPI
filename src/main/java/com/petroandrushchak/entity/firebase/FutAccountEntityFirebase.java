package com.petroandrushchak.entity.firebase;

import com.google.cloud.firestore.DocumentReference;
import lombok.Data;

@Data
public class FutAccountEntityFirebase {

    String username;
    String eaLogin;
    String eaPassword;

    String mailDocumentId;

}
