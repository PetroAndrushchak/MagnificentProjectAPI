package com.petroandrushchak.exceptions;

public class BrowserProcessNotFound extends NoSuchElementFoundException {

    public BrowserProcessNotFound(Long id) {
        super("Could not find Browser Process with id: " + id);
    }
}
