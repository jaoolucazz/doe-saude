package br.edu.unibh.doesaude.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "doacoes_doesaude")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doador_id", nullable = false)
    private Usuario doador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "necessidade_id", nullable = false)
    private Necessidade necessidade;

    @Column(name = "data_agendada", nullable = false)
    private LocalDate dataAgendada;

    @Column(nullable = false)
    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusDoacao status = StatusDoacao.AGENDADA;

    @Column(length = 1000)
    private String observacao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoPersistir() {
        this.criadoEm = Instant.now();
        if (this.status == null) {
            this.status = StatusDoacao.AGENDADA;
        }
    }
}
