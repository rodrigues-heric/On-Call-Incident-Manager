package com.rodrigues.heric.incidentmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableJpaAuditing
public class IncidentmanagerApplication {

	public static void main(String[] args) {
		loadDotEnv();

		SpringApplication.run(IncidentmanagerApplication.class, args);
	}

	private static void loadDotEnv() {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
	}

}
