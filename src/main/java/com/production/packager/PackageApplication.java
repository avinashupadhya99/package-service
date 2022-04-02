package com.production.packager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PackageApplication {

	public static void main(String[] args) {
		SpringApplication.run(PackageApplication.class, args);
	}

}
