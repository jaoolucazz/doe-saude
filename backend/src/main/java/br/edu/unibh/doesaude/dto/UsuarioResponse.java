package br.edu.unibh.doesaude.dto;

import br.edu.unibh.doesaude.domain.TipoSanguineo;
import br.edu.unibh.doesaude.domain.TipoUsuario;
import br.edu.unibh.doesaude.domain.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        TipoUsuario tipo,
        TipoSanguineo tipoSanguineo,
        String nomeInstituicao,
        String endereco,
        String fotoPerfilUrl
) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getTelefone(),
                u.getTipo(),
                u.getTipoSanguineo(),
                u.getNomeInstituicao(),
                u.getEndereco(),
                u.getFotoPerfilUrl()
        );
    }
}
