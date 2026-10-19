package br.edu.unibh.doesaude.security;

import br.edu.unibh.doesaude.domain.Usuario;
import br.edu.unibh.doesaude.exception.NaoEncontradoException;
import br.edu.unibh.doesaude.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioAtual {

    private final UsuarioRepository usuarioRepository;

    public Usuario resolver(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NaoEncontradoException("Usuário autenticado não encontrado"));
    }
}
