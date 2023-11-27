package com.petroandrushchak;

public class Test {

    public static void main(String[] args) {

        String originalURL = "https://utas.mob.v2.fut.ea.com/ut/game/fc24/trade/510603403076/bid";
        String regexPattern = "/trade/\\d+/"; // Regex pattern to match the dynamic value

        // Replace the dynamic value using regex
        String updatedURL = originalURL.replaceAll(regexPattern, "/trade/11111111/");

        System.out.println("Original URL: " + originalURL);
        System.out.println("Updated URL: " + updatedURL);


    }
}
