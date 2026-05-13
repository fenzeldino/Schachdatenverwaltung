package io.github.fenzeldino.Schachdatenverwaltung;

import io.github.fenzeldino.Schachdatenverwaltung.Model.Spieler;
import io.github.fenzeldino.Schachdatenverwaltung.Model.Turnier;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.MatchUpRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.SpielerRepository;
import io.github.fenzeldino.Schachdatenverwaltung.Repository.TurnierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SchachdatenverwaltungApplication {

	private static final Logger log = LoggerFactory.getLogger(SchachdatenverwaltungApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SchachdatenverwaltungApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(SpielerRepository spielerRepository, MatchUpRepository matchUpRepository,TurnierRepository turnierRepositroy) {
		return (args) -> {

			Spieler spieler1 = new Spieler("Kevin",2000,20);
			Spieler spieler2 = new Spieler("Markus",1200,15);
			Spieler spieler3 = new Spieler("Hans",1500,28);
			Spieler spieler4 = new Spieler("Marten",2000,22);

			spielerRepository.save(spieler1);
			spielerRepository.save(spieler2);
			spielerRepository.save(spieler3);
			spielerRepository.save(spieler4);



			Turnier turnier = new Turnier();



			turnier.setTunierspieler(spieler1);
			turnier.setTunierspieler(spieler2);
			turnier.setTunierspieler(spieler3);
			turnier.setTunierspieler(spieler4);


			turnier.createMatchUp(spieler1,spieler2);
			turnier.createMatchUp(spieler3,spieler4);

			turnierRepositroy.save(turnier);

		};
	}

}
