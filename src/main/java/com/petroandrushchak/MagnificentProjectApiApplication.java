package com.petroandrushchak;

import com.petroandrushchak.fut.configs.BrowserConfigs;
import org.aeonbits.owner.ConfigCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MagnificentProjectApiApplication {

	public static BrowserConfigs browserConfigs() {
		return ConfigCache.getOrCreate(BrowserConfigs.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(MagnificentProjectApiApplication.class, args);
	}

}
