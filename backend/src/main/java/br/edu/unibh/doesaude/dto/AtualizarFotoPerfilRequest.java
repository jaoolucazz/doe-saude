package br.edu.unibh.doesaude.dto;

import jakarta.validation.constraints.NotBlank;

public record AtualizarFotoPerfilRequest(
        @NotBlank String url
) {
}
