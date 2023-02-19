package com.petroandrushchak.fut.exeptions;

public class MatcherNotFoundException extends RuntimeException {

    public MatcherNotFoundException(String stringToBeParsed, String regex) {
        super(String.format("Can not parse '%s' using regex '%s'", stringToBeParsed, regex));
    }
}
