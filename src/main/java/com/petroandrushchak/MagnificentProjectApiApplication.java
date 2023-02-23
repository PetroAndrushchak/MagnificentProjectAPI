package com.petroandrushchak;

import com.petroandrushchak.fut.configs.BrowserConfigs;
import org.aeonbits.owner.ConfigCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class MagnificentProjectApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MagnificentProjectApiApplication.class, args);
	}

}
