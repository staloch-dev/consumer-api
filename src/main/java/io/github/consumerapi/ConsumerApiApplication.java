package io.github.consumerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class ConsumerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApiApplication.class, args);
		log.info("Application started successfully!");
	}

}
