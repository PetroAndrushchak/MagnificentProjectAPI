package com.petroandrushchak.fut.exeptions;

public class NotFoundSwitchException extends RuntimeException {

    public NotFoundSwitchException(Object whatIsNotFound) {
        super("Switch can not find any case for value: " + whatIsNotFound);
    }

}
