package br.edu.unibh.doesaude.dto;

import br.edu.unibh.doesaude.domain.*;

import java.time.Instant;

public record NecessidadeResponse(
        Long id,
        Long instituicaoId,
        String nomeInstituicao,
        TipoNecessidade tipo,
        String descricao,
        TipoSanguineo tipoSanguineo,
        String nomeItem,
        PublicoAlvo publicoAlvo,
        TipoBeneficiario tipoBeneficiario,
        String nomeBeneficiario,
        String descricaoBeneficiario,
        String fotoBeneficiarioUrl,
        Integer quantidade,
        Integer quantidadeAtendida,
        Urgencia urgencia,
        StatusNecessidade status,
        Instant criadoEm
) {
    public static NecessidadeResponse de(Necessidade n) {
        return new NecessidadeResponse(
                n.getId(),
                n.getInstituicao().getId(),
                n.getInstituicao().getNomeInstituicao(),
                n.getTipo(),
                n.getDescricao(),
                n.getTipoSanguineo(),
                n.getNomeItem(),
                n.getPublicoAlvo(),
                n.getTipoBeneficiario(),
                n.getNomeBeneficiario(),
                n.getDescricaoBeneficiario(),
                n.getFotoBeneficiarioUrl(),
                n.getQuantidade(),
                n.getQuantidadeAtendida(),
                n.getUrgencia(),
                n.getStatus(),
                n.getCriadoEm()
        );
    }
}
