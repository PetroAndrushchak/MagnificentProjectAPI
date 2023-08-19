package com.petroandrushchak.model.domain;

public class SnippingResult {

    int numberOfSearches;

    int numberOfSearchesItemFound;

    int numberOfSearchesItemBought;

    public void incrementNumberOfSearches() {
        numberOfSearches++;
    }

    public void incrementNumberOfSearchesItemFound() {
        numberOfSearchesItemFound++;
    }

    public void incrementNumberOfSearchesItemBought() {
        numberOfSearchesItemBought++;
    }


}
