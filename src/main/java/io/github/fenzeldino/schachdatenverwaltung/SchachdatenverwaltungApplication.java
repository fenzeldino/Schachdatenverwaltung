package io.github.fenzeldino.schachdatenverwaltung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchachdatenverwaltungApplication {

	private static final Logger log = LoggerFactory.getLogger(SchachdatenverwaltungApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SchachdatenverwaltungApplication.class, args);
	}

}
