package com.petroandrushchak.model.fut.snipping.filters;

public enum NationDummy {
    //        ENGLAND(54L),
//        FRANCE(14L),
    GOGOLIV(10000L);

    final Long id;

    NationDummy(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}