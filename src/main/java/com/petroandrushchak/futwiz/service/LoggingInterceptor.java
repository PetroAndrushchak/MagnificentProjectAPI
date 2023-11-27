package com.petroandrushchak.futwiz.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;


import java.io.IOException;

@Slf4j
public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        long startTime = System.nanoTime();

        System.out.println("Request: " + request.method() + " " + request.url());

        Response response = chain.proceed(request);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert nanoseconds to milliseconds

        System.out.println("Response: " + response.code() + " in " + duration + "ms");

        return response;
    }
}