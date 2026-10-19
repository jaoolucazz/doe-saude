package br.edu.unibh.doesaude.controller;

import br.edu.unibh.doesaude.domain.Usuario;
import br.edu.unibh.doesaude.dto.AtualizarFotoPerfilRequest;
import br.edu.unibh.doesaude.dto.UsuarioResponse;
import br.edu.unibh.doesaude.security.UsuarioAtual;
import br.edu.unibh.doesaude.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioAtual usuarioAtual;
    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public UsuarioResponse meuPerfil(Authentication authentication) {
        Usuario usuario = usuarioAtual.resolver(authentication);
        return UsuarioResponse.de(usuario);
    }

    @PatchMapping("/me/foto")
    public UsuarioResponse atualizarFoto(Authentication authentication, @Valid @RequestBody AtualizarFotoPerfilRequest request) {
        Usuario usuario = usuarioAtual.resolver(authentication);
        return usuarioService.atualizarFotoPerfil(usuario, request.url());
    }
}
