package br.edu.unibh.doesaude.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DoacaoRequest(
        @NotNull Long necessidadeId,
        @NotNull @Min(1) Integer quantidade,
        @NotNull @FutureOrPresent LocalDate dataAgendada,
        String observacao
) {
}
