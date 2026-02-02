package com.example.todos.simpletodoservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Simple Todo Service API",
				description = "Endpoints for managing todo items and service health."
		)
)
public class SimpleTodoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleTodoServiceApplication.class, args);
	}

}
