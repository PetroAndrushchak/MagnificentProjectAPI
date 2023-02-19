package com.petroandrushchak.helper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomHelper {

    public static int getRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }

}
