package com.petroandrushchak.exceptions;

public class FutEaAccountNotFound extends RuntimeException {

    public FutEaAccountNotFound(String id) {
        super("Could not find employee " + id);
    }
}
