package com.blamovi.apiblamovi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API Blámovi", version = "1.0.0", description = "Swagger da API Blámovi"))
public class ApiblamoviApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiblamoviApplication.class, args);
	}

}
