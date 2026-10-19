package br.edu.unibh.doesaude.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "necessidades_doesaude")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Necessidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Usuario instituicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNecessidade tipo;

    @Column(nullable = false, length = 2000)
    private String descricao;

    // Preenchido quando tipo == SANGUE
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sanguineo")
    private TipoSanguineo tipoSanguineo;

    // Preenchido quando tipo == MEDICAMENTO ou OUTRO
    @Column(name = "nome_item")
    private String nomeItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "publico_alvo", nullable = false)
    private PublicoAlvo publicoAlvo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_beneficiario", nullable = false)
    private TipoBeneficiario tipoBeneficiario;

    @Column(name = "nome_beneficiario", nullable = false)
    private String nomeBeneficiario;

    @Column(name = "descricao_beneficiario", length = 2000)
    private String descricaoBeneficiario;

    @Column(name = "foto_beneficiario_url")
    private String fotoBeneficiarioUrl;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "quantidade_atendida", nullable = false)
    @Builder.Default
    private Integer quantidadeAtendida = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Urgencia urgencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusNecessidade status = StatusNecessidade.ABERTA;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoPersistir() {
        this.criadoEm = Instant.now();
        if (this.status == null) {
            this.status = StatusNecessidade.ABERTA;
        }
        if (this.quantidadeAtendida == null) {
            this.quantidadeAtendida = 0;
        }
    }
}
