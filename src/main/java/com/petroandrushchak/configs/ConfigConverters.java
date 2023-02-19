package com.petroandrushchak.configs;


import com.petroandrushchak.exceptions.ConfigConverterException;
import com.petroandrushchak.exceptions.NotValidConfigException;
import lombok.experimental.UtilityClass;
import org.aeonbits.owner.Converter;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@UtilityClass
public class ConfigConverters {

    public static class ImplicitWaitConverter implements Converter<Duration> {
        @Override
        public Duration convert(Method method, String input) {
            if (!NumberUtils.isCreatable(input)) {
                throw new NotValidConfigException("The Implicit wait is not in number format: " + input);
            }
            int seconds = NumberUtils.createInteger(input);
            if (seconds > 60) {
                throw new NotValidConfigException("The Implicit wait can not be more than 60 seconds, provided: " + seconds);
            }
            return Duration.ofSeconds(seconds);
        }
    }


    public static class URLConverter implements Converter<URL> {
        @Override
        public URL convert(Method method, String input) {
            try {
                return new URL(input);
            } catch (MalformedURLException e) {
                throw new ConfigConverterException("Exception during converting String URL: " + input, e);
            }
        }
    }
}
