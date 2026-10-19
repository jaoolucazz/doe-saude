package br.edu.unibh.doesaude.controller;

import br.edu.unibh.doesaude.domain.Usuario;
import br.edu.unibh.doesaude.dto.DoacaoRequest;
import br.edu.unibh.doesaude.dto.DoacaoResponse;
import br.edu.unibh.doesaude.dto.StatusDoacaoRequest;
import br.edu.unibh.doesaude.security.UsuarioAtual;
import br.edu.unibh.doesaude.service.DoacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doacoes")
@RequiredArgsConstructor
public class DoacaoController {

    private final DoacaoService doacaoService;
    private final UsuarioAtual usuarioAtual;

    @PostMapping
    public ResponseEntity<DoacaoResponse> agendar(
            Authentication authentication,
            @Valid @RequestBody DoacaoRequest request
    ) {
        Usuario doador = usuarioAtual.resolver(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(doacaoService.agendar(doador, request));
    }

    @GetMapping("/minhas")
    public List<DoacaoResponse> listarMinhas(Authentication authentication) {
        Usuario doador = usuarioAtual.resolver(authentication);
        return doacaoService.listarPorDoador(doador.getId());
    }

    @GetMapping("/recebidas")
    public List<DoacaoResponse> listarRecebidas(Authentication authentication) {
        Usuario instituicao = usuarioAtual.resolver(authentication);
        return doacaoService.listarPorInstituicao(instituicao.getId());
    }

    @PatchMapping("/{id}/status")
    public DoacaoResponse atualizarStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody StatusDoacaoRequest request
    ) {
        Usuario instituicao = usuarioAtual.resolver(authentication);
        return doacaoService.atualizarStatus(id, instituicao, request.status());
    }

    @PatchMapping("/{id}/cancelar")
    public DoacaoResponse cancelar(Authentication authentication, @PathVariable Long id) {
        Usuario doador = usuarioAtual.resolver(authentication);
        return doacaoService.cancelarPorDoador(id, doador);
    }
}
