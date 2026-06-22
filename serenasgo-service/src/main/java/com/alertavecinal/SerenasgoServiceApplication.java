package com.alertavecinal;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@EnableFeignClients
@SpringBootApplication
public class SerenasgoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SerenasgoServiceApplication.class, args);
	}

}
