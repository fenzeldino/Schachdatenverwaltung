package io.github.fenzeldino.Schachdatenverwaltung.DTO.Request.MatchUp;

public record MatchUpUpdateDTO(Integer MatchUpId,
                               Integer spielerEinsId,
                               Integer spielerZweiId,
                               Integer turnierId,
                               Integer gewinnerId) {
}
