package io.github.consumerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class ConsumerApiApplication {

	public static void main(String[] args) {
		var context =SpringApplication.run(ConsumerApiApplication.class, args);
		String port = context.getEnvironment().getProperty("server.port", "8080");
		log.info("Application started successfully!");
		log.info("Swagger UI: http://localhost:{}/swagger-ui.html", port);

	}

}
