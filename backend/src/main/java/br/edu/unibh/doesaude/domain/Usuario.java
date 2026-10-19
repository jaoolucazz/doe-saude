package br.edu.unibh.doesaude.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "usuarios_doesaude")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    private String telefone;

    // Campos usados somente quando tipo == DOADOR
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sanguineo")
    private TipoSanguineo tipoSanguineo;

    // Campos usados somente quando tipo == INSTITUICAO
    @Column(name = "nome_instituicao")
    private String nomeInstituicao;

    private String endereco;

    @Column(name = "foto_perfil_url")
    private String fotoPerfilUrl;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoPersistir() {
        this.criadoEm = Instant.now();
    }
}
