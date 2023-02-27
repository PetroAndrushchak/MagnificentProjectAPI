package com.petroandrushchak.exceptions;

public class ItemMappingException extends RuntimeException {

    public ItemMappingException(String fieldName, String errorMessage) {
        super("Field: '" + fieldName + "' : "  + errorMessage);
    }
}
