package com.petroandrushchak.fut.exeptions;

public class EmailClientException extends RuntimeException {

    public EmailClientException(String reasonOfFail) {
        super(reasonOfFail);
    }

}
