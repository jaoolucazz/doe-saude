package br.edu.unibh.doesaude.dto;

import br.edu.unibh.doesaude.domain.TipoSanguineo;
import br.edu.unibh.doesaude.domain.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String senha,
        @NotNull TipoUsuario tipo,
        String telefone,
        TipoSanguineo tipoSanguineo,
        String nomeInstituicao,
        String endereco
) {
}
