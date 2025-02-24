package com.tuskerlanka.tourismmangement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing(auditorAwareRef = "auditorAwareUtil")
public class TouristManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TouristManagementApplication.class, args);
	}

}
