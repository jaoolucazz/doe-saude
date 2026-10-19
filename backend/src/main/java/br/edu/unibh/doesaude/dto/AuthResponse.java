package br.edu.unibh.doesaude.dto;

import br.edu.unibh.doesaude.domain.TipoUsuario;

public record AuthResponse(
        String token,
        Long id,
        String nome,
        String email,
        TipoUsuario tipo
) {
}
