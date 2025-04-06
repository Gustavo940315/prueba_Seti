package com.seti.ms.franchises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class MsFranchisesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsFranchisesApplication.class, args);
	}

}
