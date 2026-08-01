package io.github.fenzeldino.schachdatenverwaltung.dto.request.matchUp;

public record MatchUpUpdateDTO(Integer MatchUpId,
                               Integer spielerEinsId,
                               Integer spielerZweiId,
                               Integer turnierId,
                               Integer gewinnerId) {
}
