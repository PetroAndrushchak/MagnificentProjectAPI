package com.petroandrushchak.helper;

import com.petroandrushchak.fut.exeptions.MatcherNotFoundException;
import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class StringHelper {

    public static String removeUselessWhiteSpaces(String text) {
        return text.replaceAll("\\s+", " ");
    }
    public static String getEmailDomain(String someEmail) {
        return someEmail.substring(someEmail.indexOf("@") + 1);
    }

    public static String findValueByRegexInString(String string, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(string);
        if (matcher.find()) {
            return matcher.group().strip();
        }
        throw new MatcherNotFoundException(string, regex);
    }

}
