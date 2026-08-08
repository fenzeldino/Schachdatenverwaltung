package io.github.fenzeldino.schachdatenverwaltung.service;

import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinCreateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.request.verein.VereinUpdateDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler.SpielerResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.dto.response.verein.VereinResponseDTO;
import io.github.fenzeldino.schachdatenverwaltung.mapper.SpielerMapper;
import io.github.fenzeldino.schachdatenverwaltung.mapper.VereinMapper;
import io.github.fenzeldino.schachdatenverwaltung.model.Spieler;
import io.github.fenzeldino.schachdatenverwaltung.model.Verein;
import io.github.fenzeldino.schachdatenverwaltung.repository.SpielerRepository;
import io.github.fenzeldino.schachdatenverwaltung.repository.VereinRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VereinService {

    private final VereinRepository vereinRepository;
    private final SpielerRepository spielerRepository;

    public VereinService(VereinRepository vereinRepository, SpielerRepository spielerRepository) {
        this.vereinRepository = vereinRepository;
        this.spielerRepository = spielerRepository;
    }

    @Transactional
    public VereinResponseDTO createVerein(VereinCreateDTO vereinDTO) {
        if (vereinDTO == null || vereinDTO.name() == null || vereinDTO.name().isBlank()) {
            throw new IllegalArgumentException("Ein Verein braucht einen Namen");
        }

        if (vereinRepository.existsByNameIgnoreCase(vereinDTO.name())) {
            throw new IllegalArgumentException("Verein mit Namen '" + vereinDTO.name() + "' existiert bereits");
        }

        Verein gespeichert = vereinRepository.save(VereinMapper.toEntity(vereinDTO));
        return VereinMapper.toDto(gespeichert);
    }

    @Transactional(readOnly = true)
    public List<VereinResponseDTO> getAllVereine() {
        return vereinRepository.findAll()
                .stream()
                .map(VereinMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VereinResponseDTO getVerein(int id) {
        return vereinRepository.findById(id)
                .map(VereinMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Verein mit Id: " + id + " wurde nicht gefunden"));
    }

    /**
     * Der eigentliche Zweck der Vereins-Beziehung: alle Spieler eines Vereins.
     * Spring Data joint dafür über die Fremdschlüsselspalte verein_id.
     */
    @Transactional(readOnly = true)
    public List<SpielerResponseDTO> getSpielerImVerein(int vereinId) {
        if (!vereinRepository.existsById(vereinId)) {
            throw new IllegalArgumentException("Verein mit Id: " + vereinId + " wurde nicht gefunden");
        }

        return spielerRepository.findByVerein_VereinId(vereinId)
                .stream()
                .map(SpielerMapper::toDto)
                .collect(Collectors.toList());
    }

    /** Ordnet einen bestehenden Spieler einem Verein zu. */
    @Transactional
    public SpielerResponseDTO spielerZuweisen(int vereinId, int spielerId) {
        Verein verein = vereinRepository.findById(vereinId)
                .orElseThrow(() -> new IllegalArgumentException("Verein mit Id: " + vereinId + " wurde nicht gefunden"));

        Spieler spieler = spielerRepository.findById(spielerId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler mit Id: " + spielerId + " wurde nicht gefunden"));

        spieler.setVerein(verein);
        return SpielerMapper.toDto(spielerRepository.save(spieler));
    }

    /** Löst die Vereinszugehörigkeit eines Spielers wieder auf. */
    @Transactional
    public SpielerResponseDTO spielerEntfernen(int spielerId) {
        Spieler spieler = spielerRepository.findById(spielerId)
                .orElseThrow(() -> new IllegalArgumentException("Spieler mit Id: " + spielerId + " wurde nicht gefunden"));

        spieler.setVerein(null);
        return SpielerMapper.toDto(spielerRepository.save(spieler));
    }

    @Transactional
    public VereinResponseDTO updateVerein(int id, VereinUpdateDTO vereinDTO) {
        Verein existing = vereinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Verein mit Id: " + id + " wurde nicht gefunden"));

        existing.setName(vereinDTO.name());
        existing.setZpsCode(vereinDTO.zpsCode());
        return VereinMapper.toDto(vereinRepository.save(existing));
    }

    /**
     * Löscht einen Verein. Zugeordnete Spieler bleiben erhalten und verlieren
     * lediglich ihre Zuordnung — ein Verein zu löschen darf keine Spieler-Daten
     * vernichten.
     */
    @Transactional
    public void deleteVerein(int id) {
        Verein verein = vereinRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Verein mit Id: " + id + " wurde nicht gefunden"));

        List<Spieler> zugeordnete = spielerRepository.findByVerein_VereinId(id);
        zugeordnete.forEach(spieler -> spieler.setVerein(null));
        spielerRepository.saveAll(zugeordnete);

        vereinRepository.delete(verein);
    }
}
