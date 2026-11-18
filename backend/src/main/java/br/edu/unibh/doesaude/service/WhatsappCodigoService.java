package br.edu.unibh.doesaude.service;

import br.edu.unibh.doesaude.domain.CodigoSenha;
import br.edu.unibh.doesaude.domain.Usuario;
import br.edu.unibh.doesaude.exception.RegraNegocioException;
import br.edu.unibh.doesaude.repository.CodigoSenhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

// gera o código de 6 dígitos pra redefinir senha e manda pelo mesmo webhook n8n (evento "reset_senha")
// já usado no sistema gestoo/atividades-dks
@Service
@RequiredArgsConstructor
public class WhatsappCodigoService {

    private static final int VALIDADE_MINUTOS = 10;
    private static final int MAX_TENTATIVAS = 5;
    private static final int REENVIO_SEGUNDOS = 60;

    private final CodigoSenhaRepository codigoSenhaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    @Value("${app.n8n.webhook-url}")
    private String webhookUrl;

    public void enviarCodigo(Usuario usuario) {
        String telefone = telefoneWhatsapp(usuario.getTelefone());
        if (telefone == null) return;

        CodigoSenha existente = codigoSenhaRepository.findById(usuario.getId()).orElse(null);
        if (existente != null && existente.getCriadoEm().isAfter(Instant.now().minusSeconds(REENVIO_SEGUNDOS))) {
            throw new RegraNegocioException("aguarde um minuto antes de pedir outro código");
        }

        String codigo = String.format("%06d", random.nextInt(1_000_000));
        codigoSenhaRepository.save(CodigoSenha.builder()
                .usuarioId(usuario.getId())
                .codigoHash(passwordEncoder.encode(codigo))
                .expiraEm(Instant.now().plusSeconds(VALIDADE_MINUTOS * 60L))
                .tentativas(0)
                .criadoEm(Instant.now())
                .build());

        try {
            String corpo = """
                    {"evento":"reset_senha","nome":"%s","telefone":"%s","codigo":"%s","validadeMinutos":%d}"""
                    .formatted(escaparJson(usuario.getNome()), telefone, codigo, VALIDADE_MINUTOS);
            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(corpo))
                    .build();
            HttpResponse<Void> resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.discarding());
            if (resposta.statusCode() >= 300) {
                throw new IllegalStateException("webhook respondeu " + resposta.statusCode());
            }
        } catch (Exception e) {
            codigoSenhaRepository.deleteById(usuario.getId());
            throw new RegraNegocioException("não foi possível enviar o código agora, tente novamente em instantes");
        }
    }

    // confere o código e, se bater, apaga o registro; qualquer falha (sem código pendente, expirado,
    // tentativas esgotadas ou código errado) devolve false com a mesma mensagem genérica no chamador,
    // pra não revelar se o e-mail existe
    public boolean confirmarCodigo(Long usuarioId, String codigoInformado) {
        CodigoSenha entidade = codigoSenhaRepository.findById(usuarioId).orElse(null);
        if (entidade == null) return false;

        if (entidade.getExpiraEm().isBefore(Instant.now()) || entidade.getTentativas() >= MAX_TENTATIVAS) {
            codigoSenhaRepository.deleteById(usuarioId);
            return false;
        }

        boolean confere = codigoInformado != null && codigoInformado.matches("\\d{6}")
                && passwordEncoder.matches(codigoInformado, entidade.getCodigoHash());
        if (!confere) {
            entidade.setTentativas(entidade.getTentativas() + 1);
            codigoSenhaRepository.save(entidade);
            return false;
        }

        codigoSenhaRepository.deleteById(usuarioId);
        return true;
    }

    // telefone salvo com DDD (10-11 dígitos) vira 55 + DDD + número, formato do envio de WhatsApp
    private String telefoneWhatsapp(String telefone) {
        if (telefone == null) return null;
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.isBlank()) return null;
        return digitos.length() <= 11 ? "55" + digitos : digitos;
    }

    private String escaparJson(String texto) {
        return texto == null ? "" : texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
