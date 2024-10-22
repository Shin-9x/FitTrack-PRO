package it.fartingbrains.fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableReactiveFeignClients
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
