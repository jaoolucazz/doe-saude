package br.edu.unibh.doesaude.dto;

import br.edu.unibh.doesaude.domain.StatusDoacao;
import jakarta.validation.constraints.NotNull;

public record StatusDoacaoRequest(
        @NotNull StatusDoacao status
) {
}
