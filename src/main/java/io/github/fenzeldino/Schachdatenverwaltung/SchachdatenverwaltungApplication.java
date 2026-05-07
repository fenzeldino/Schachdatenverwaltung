package io.github.fenzeldino.Schachdatenverwaltung;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SchachdatenverwaltungApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringDataBikeDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SchachdatenverwaltungApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(BikeRepository bikeRepository, OwnerRepository ownerRepository) {
		return (args) -> {

			Spieler spieler1 = new Spieler("Kevin",2000,20);
			Spieler spieler2 = new Spieler("Markus",1200,15);
			Spieler spieler3 = new Spieler("Hans",1500,28);

		};
	}

}
