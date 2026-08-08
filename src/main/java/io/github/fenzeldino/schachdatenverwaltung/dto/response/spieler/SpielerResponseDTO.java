package io.github.fenzeldino.schachdatenverwaltung.dto.response.spieler;


import java.util.List;

/**
 * @param vereinId   Id des Vereins, oder null wenn der Spieler keinem zugeordnet ist
 * @param vereinName Name des Vereins, oder null — direkt mitgeliefert, damit die
 *                   Oberfläche für eine Spielerliste nicht je Zeile nachladen muss
 */
public record SpielerResponseDTO(Integer id,
                                 String name,
                                 Double rating,
                                 List<Integer> TurnierIds,
                                 Integer vereinId,
                                 String vereinName) {

    /**
     * Kurzform ohne Vereinsangabe. Hält bestehende Aufrufstellen kompilierbar,
     * die vor Einführung der Vereins-Beziehung entstanden sind.
     */
    public SpielerResponseDTO(Integer id, String name, Double rating, List<Integer> TurnierIds) {
        this(id, name, rating, TurnierIds, null, null);
    }
}
