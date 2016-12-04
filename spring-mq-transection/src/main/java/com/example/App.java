package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	public static final String MSGS = "messages";

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}