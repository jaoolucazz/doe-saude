package br.edu.unibh.doesaude.controller;

import br.edu.unibh.doesaude.dto.UploadResponse;
import br.edu.unibh.doesaude.service.ArmazenamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final ArmazenamentoService armazenamentoService;

    @PostMapping
    public UploadResponse enviar(@RequestParam("arquivo") MultipartFile arquivo) {
        return new UploadResponse(armazenamentoService.salvar(arquivo));
    }
}
