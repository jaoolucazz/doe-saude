package br.edu.unibh.doesaude.service;

import br.edu.unibh.doesaude.exception.RegraNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ArmazenamentoService {

    private static final List<String> TIPOS_PERMITIDOS = List.of("image/jpeg", "image/png", "image/webp");

    private final Path diretorioBase;

    public ArmazenamentoService(@Value("${app.upload.dir}") String diretorio) {
        this.diretorioBase = Path.of(diretorio).toAbsolutePath().normalize();
        try {
            Files.createDirectories(diretorioBase);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar o diretório de uploads: " + diretorioBase, e);
        }
    }

    public String salvar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException("Envie um arquivo de imagem");
        }
        if (!TIPOS_PERMITIDOS.contains(arquivo.getContentType())) {
            throw new RegraNegocioException("Formato de imagem não suportado. Use JPEG, PNG ou WebP");
        }

        String extensao = switch (arquivo.getContentType()) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
        String nomeArquivo = UUID.randomUUID() + extensao;
        Path destino = diretorioBase.resolve(nomeArquivo);

        try {
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao salvar o arquivo enviado", e);
        }

        return "/uploads/" + nomeArquivo;
    }
}
