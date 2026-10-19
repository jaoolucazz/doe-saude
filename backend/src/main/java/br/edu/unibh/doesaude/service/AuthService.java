package br.edu.unibh.doesaude.service;

import br.edu.unibh.doesaude.domain.TipoUsuario;
import br.edu.unibh.doesaude.domain.Usuario;
import br.edu.unibh.doesaude.dto.AuthResponse;
import br.edu.unibh.doesaude.dto.LoginRequest;
import br.edu.unibh.doesaude.dto.RegistroRequest;
import br.edu.unibh.doesaude.exception.RegraNegocioException;
import br.edu.unibh.doesaude.repository.UsuarioRepository;
import br.edu.unibh.doesaude.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail");
        }
        if (request.tipo() == TipoUsuario.INSTITUICAO && (request.nomeInstituicao() == null || request.nomeInstituicao().isBlank())) {
            throw new RegraNegocioException("Informe o nome da instituição");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .tipo(request.tipo())
                .telefone(request.telefone())
                .tipoSanguineo(request.tipoSanguineo())
                .nomeInstituicao(request.nomeInstituicao())
                .endereco(request.endereco())
                .build();

        usuario = usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario.getEmail(), Map.of("tipo", usuario.getTipo().name()));
        return new AuthResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTipo());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha inválidos"));

        String token = jwtService.gerarToken(usuario.getEmail(), Map.of("tipo", usuario.getTipo().name()));
        return new AuthResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTipo());
    }
}
