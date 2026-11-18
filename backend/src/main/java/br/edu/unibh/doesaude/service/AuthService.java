package br.edu.unibh.doesaude.service;

import br.edu.unibh.doesaude.domain.TipoUsuario;
import br.edu.unibh.doesaude.domain.Usuario;
import br.edu.unibh.doesaude.dto.AuthResponse;
import br.edu.unibh.doesaude.dto.EsqueciSenhaRequest;
import br.edu.unibh.doesaude.dto.LoginRequest;
import br.edu.unibh.doesaude.dto.RedefinirSenhaRequest;
import br.edu.unibh.doesaude.dto.RegistroRequest;
import br.edu.unibh.doesaude.exception.RegraNegocioException;
import br.edu.unibh.doesaude.repository.UsuarioRepository;
import br.edu.unibh.doesaude.security.JwtService;
import br.edu.unibh.doesaude.util.CnpjUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WhatsappCodigoService whatsappCodigoService;

    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail");
        }

        String cnpj = null;
        if (request.tipo() == TipoUsuario.INSTITUICAO) {
            if (request.nomeInstituicao() == null || request.nomeInstituicao().isBlank()) {
                throw new RegraNegocioException("Informe o nome da instituição");
            }
            cnpj = CnpjUtils.normalizarSeValido(request.cnpj());
            if (cnpj == null) {
                throw new RegraNegocioException("Informe um CNPJ válido");
            }
            if (usuarioRepository.existsByCnpj(cnpj)) {
                throw new RegraNegocioException("Já existe uma instituição cadastrada com este CNPJ");
            }
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .tipo(request.tipo())
                .telefone(request.telefone())
                .tipoSanguineo(request.tipoSanguineo())
                .nomeInstituicao(request.nomeInstituicao())
                .cnpj(cnpj)
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

    // resposta é sempre a mesma pra quem chama, exista ou não o e-mail e tenha ou não telefone
    // cadastrado — só assim a tela de login não revela se uma conta existe
    public void esqueciSenha(EsqueciSenhaRequest request) {
        usuarioRepository.findByEmail(request.email()).ifPresent(usuario -> {
            try {
                whatsappCodigoService.enviarCodigo(usuario);
            } catch (Exception e) {
                log.warn("falha ao enviar código de redefinição de senha: {}", e.getMessage());
            }
        });
    }

    @Transactional
    public void redefinirSenha(RedefinirSenhaRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElse(null);
        boolean confirmado = usuario != null && whatsappCodigoService.confirmarCodigo(usuario.getId(), request.codigo());
        if (!confirmado) {
            throw new RegraNegocioException("Código incorreto ou expirado — peça um novo");
        }
        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
    }
}
