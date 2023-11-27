package com.petroandrushchak.service;

import com.petroandrushchak.futwiz.service.LoggingInterceptor;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Component;

@Component
public class FutWizService {


    @SneakyThrows
    public String getPageForUrl(String url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new LoggingInterceptor());

        OkHttpClient client = new OkHttpClient(builder);

        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();

        try (var response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
