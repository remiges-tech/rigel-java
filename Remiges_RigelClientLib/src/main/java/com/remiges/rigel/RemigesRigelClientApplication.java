package com.remiges.rigel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RemigesRigelClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemigesRigelClientApplication.class, args);
	}

}
