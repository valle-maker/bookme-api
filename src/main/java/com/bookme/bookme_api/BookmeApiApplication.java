package com.bookme.bookme_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookmeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookmeApiApplication.class, args);
	}

}
