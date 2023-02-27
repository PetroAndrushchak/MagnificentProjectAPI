package com.petroandrushchak.exceptions;

public class ItemMappingException extends RuntimeException {

    public ItemMappingException(String fieldName, String errorMessage) {
        super("Item mapping exception for field: " + fieldName + " with error: " + errorMessage);
    }
}
