package br.edu.unibh.doesaude.controller;

import br.edu.unibh.doesaude.dto.AuthResponse;
import br.edu.unibh.doesaude.dto.EsqueciSenhaRequest;
import br.edu.unibh.doesaude.dto.LoginRequest;
import br.edu.unibh.doesaude.dto.RedefinirSenhaRequest;
import br.edu.unibh.doesaude.dto.RegistroRequest;
import br.edu.unibh.doesaude.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<Map<String, Boolean>> esqueciSenha(@Valid @RequestBody EsqueciSenhaRequest request) {
        authService.esqueciSenha(request);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/esqueci-senha/redefinir")
    public ResponseEntity<Map<String, Boolean>> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        authService.redefinirSenha(request);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
