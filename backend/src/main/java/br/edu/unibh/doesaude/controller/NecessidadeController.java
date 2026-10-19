package br.edu.unibh.doesaude.controller;

import br.edu.unibh.doesaude.domain.PublicoAlvo;
import br.edu.unibh.doesaude.domain.StatusNecessidade;
import br.edu.unibh.doesaude.domain.TipoNecessidade;
import br.edu.unibh.doesaude.domain.TipoSanguineo;
import br.edu.unibh.doesaude.domain.Usuario;
import br.edu.unibh.doesaude.dto.NecessidadeRequest;
import br.edu.unibh.doesaude.dto.NecessidadeResponse;
import br.edu.unibh.doesaude.security.UsuarioAtual;
import br.edu.unibh.doesaude.service.NecessidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/necessidades")
@RequiredArgsConstructor
public class NecessidadeController {

    private final NecessidadeService necessidadeService;
    private final UsuarioAtual usuarioAtual;

    @GetMapping
    public List<NecessidadeResponse> listar(
            @RequestParam(required = false) TipoNecessidade tipo,
            @RequestParam(required = false) TipoSanguineo tipoSanguineo,
            @RequestParam(required = false) PublicoAlvo publicoAlvo,
            @RequestParam(required = false) StatusNecessidade status
    ) {
        return necessidadeService.listar(tipo, tipoSanguineo, publicoAlvo, status);
    }

    @GetMapping("/{id}")
    public NecessidadeResponse buscarPorId(@PathVariable Long id) {
        return necessidadeService.buscarPorId(id);
    }

    @GetMapping("/minhas")
    public List<NecessidadeResponse> listarMinhas(Authentication authentication) {
        Usuario instituicao = usuarioAtual.resolver(authentication);
        return necessidadeService.listarPorInstituicao(instituicao.getId());
    }

    @PostMapping
    public ResponseEntity<NecessidadeResponse> criar(
            Authentication authentication,
            @Valid @RequestBody NecessidadeRequest request
    ) {
        Usuario instituicao = usuarioAtual.resolver(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(necessidadeService.criar(instituicao, request));
    }

    @PatchMapping("/{id}/cancelar")
    public NecessidadeResponse cancelar(Authentication authentication, @PathVariable Long id) {
        Usuario instituicao = usuarioAtual.resolver(authentication);
        return necessidadeService.cancelar(id, instituicao);
    }
}
