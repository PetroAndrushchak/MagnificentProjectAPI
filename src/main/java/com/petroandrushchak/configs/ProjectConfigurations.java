package com.petroandrushchak.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.petroandrushchak.fut.configs.BrowserConfigs;
import com.petroandrushchak.helper.ProjectHelper;
import com.petroandrushchak.process.BrowserProcessHelper;
import lombok.SneakyThrows;
import org.aeonbits.owner.ConfigCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.io.FileInputStream;


@Configuration
public class ProjectConfigurations {


    @SneakyThrows
    @Bean
    public Firestore firebaseApp() {
        FileInputStream serviceAccount =
                new FileInputStream(ProjectHelper.getRootProjectFolderPath() + "/firebaseKeys.json");

        FirebaseOptions options = FirebaseOptions.builder()
                                                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                                                 .build();
        FirebaseApp.initializeApp(options);
        return FirestoreClient.getFirestore();
    }

    public static BrowserConfigs browserConfigs() {
        return ConfigCache.getOrCreate(BrowserConfigs.class);
    }

    @Bean
    @Scope("singleton")
    public BrowserProcessHelper browserProcessHelper() {
        return new BrowserProcessHelper();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        //TODO: add serializers
        // module.addSerializer(LOCAL_DATETIME_SERIALIZER);
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .registerModule(module);
    }

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Magnificent-");
        executor.initialize();
        return executor;
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }
}