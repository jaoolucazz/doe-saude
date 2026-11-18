package br.edu.unibh.doesaude.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequest(
        @NotBlank @Email String email,
        @NotBlank String codigo,
        @NotBlank @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String novaSenha
) {
}
