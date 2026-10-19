package br.edu.unibh.doesaude.service;

import br.edu.unibh.doesaude.domain.Usuario;
import br.edu.unibh.doesaude.dto.UsuarioResponse;
import br.edu.unibh.doesaude.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponse atualizarFotoPerfil(Usuario usuario, String url) {
        usuario.setFotoPerfilUrl(url);
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }
}
