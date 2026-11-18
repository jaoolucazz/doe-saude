package br.edu.unibh.doesaude.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// código de 6 dígitos (guardado só como hash) pra redefinir a senha pelo WhatsApp; uma linha por
// usuário, sempre sobrescrita ao pedir outro código e apagada quando a senha é trocada
@Entity
@Table(name = "codigos_senha_doesaude")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodigoSenha {

    @Id
    private Long usuarioId;

    @Column(name = "codigo_hash", nullable = false)
    private String codigoHash;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(nullable = false)
    private int tentativas;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;
}
