package br.edu.unibh.doesaude.dto;

import br.edu.unibh.doesaude.domain.PublicoAlvo;
import br.edu.unibh.doesaude.domain.TipoBeneficiario;
import br.edu.unibh.doesaude.domain.TipoNecessidade;
import br.edu.unibh.doesaude.domain.TipoSanguineo;
import br.edu.unibh.doesaude.domain.Urgencia;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NecessidadeRequest(
        @NotNull TipoNecessidade tipo,
        @NotBlank String descricao,
        TipoSanguineo tipoSanguineo,
        String nomeItem,
        @NotNull PublicoAlvo publicoAlvo,
        @NotNull TipoBeneficiario tipoBeneficiario,
        @NotBlank String nomeBeneficiario,
        String descricaoBeneficiario,
        String fotoBeneficiarioUrl,
        @NotNull @Min(1) Integer quantidade,
        @NotNull Urgencia urgencia
) {
}
