package com.rodrigues.heric.incidentmanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class IncidentmanagerApplicationTests {

	@BeforeAll
	static void setup() {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
	}

	@Test
	void contextLoads() {
	}

}
