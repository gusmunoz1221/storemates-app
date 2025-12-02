package com.storemates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StorematesApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorematesApplication.class, args);
	}

}
