package com.petroandrushchak.exceptions;

public class BrowserProcessFutAccountBlocked extends RuntimeException {

    public BrowserProcessFutAccountBlocked(String id) {
        super("FUT Account with id: " + id + " is currently used by another Browser Process");
    }
}
