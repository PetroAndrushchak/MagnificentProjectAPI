package com.petroandrushchak.helper;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ListHelper {

    public static <T> List<T> findDuplicates(List<T> list) {

        List<T> duplicates = new ArrayList<>();

        CollectionUtils.getCardinalityMap(list).forEach((customObject, count) -> {
            // If count is greater than 1, consider it a duplicate
            if (count > 1) {
                duplicates.add(customObject);
            }
        });

        return duplicates;
    }
}
