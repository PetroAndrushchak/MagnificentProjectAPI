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

    public static Long getDigitFromString(String inputString) {
        inputString = inputString.replaceAll("\\D+", "");
        return Long.parseLong(inputString);
    }

    public static long getIntegerAtEndOfString(String inputString) {
        int integerValue = 0;
        int length = inputString.length();

        for (int i = length - 1; i >= 0; i--) {
            char currentChar = inputString.charAt(i);

            if (Character.isDigit(currentChar)) {
                // If the current character is a digit, add it to the integer value
                integerValue += (currentChar - '0') * Math.pow(10, length - i - 1);
            } else if (integerValue > 0) {
                // If we've already started building the integer value and we've encountered a non-digit character,
                // we've reached the end of the integer value and can break out of the loop
                break;
            }
        }

        return integerValue;
    }


}
