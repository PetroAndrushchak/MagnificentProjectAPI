package com.petroandrushchak.exceptions;

public class FutEaAccountNotFound extends NoSuchElementFoundException {

    public FutEaAccountNotFound(Long id) {
        super("Could not find FUT EA Account with id: " + id);
    }
}
