package com.petroandrushchak.fut.snipping.bot;

import lombok.SneakyThrows;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

public class SendExcludedPlayers {

    @SneakyThrows
    public static void main(String[] args) {

        //Step 1. Get Code's decoded string
        String encodedBody = "";
        String decodedBody = new String(Base64.getDecoder().decode(encodedBody));
        String urlDecodedString = URLDecoder.decode(decodedBody, "UTF-8");

        //Step 2 Modify Json and add new players
       String modifiedString = "";
       String urcEncodedString = URLEncoder.encode(modifiedString, "UTF-8");
        urcEncodedString = urcEncodedString.replaceAll("\\+", "%20");
        String decodedNewBody = new String(Base64.getEncoder().encode(urcEncodedString.getBytes()));

        boolean isEqual = decodedBody.equals(urcEncodedString);

        System.out.println(urcEncodedString);

            //{"name":"Cristiano Ronaldo","rating":86,"id":20801}

    }

}
