package br.edu.unibh.doesaude.dto;

import br.edu.unibh.doesaude.domain.Doacao;
import br.edu.unibh.doesaude.domain.StatusDoacao;

import java.time.Instant;
import java.time.LocalDate;

public record DoacaoResponse(
        Long id,
        Long doadorId,
        String doadorNome,
        Long necessidadeId,
        String necessidadeDescricao,
        Integer quantidade,
        LocalDate dataAgendada,
        StatusDoacao status,
        String observacao,
        Instant criadoEm
) {
    public static DoacaoResponse de(Doacao d) {
        return new DoacaoResponse(
                d.getId(),
                d.getDoador().getId(),
                d.getDoador().getNome(),
                d.getNecessidade().getId(),
                d.getNecessidade().getDescricao(),
                d.getQuantidade(),
                d.getDataAgendada(),
                d.getStatus(),
                d.getObservacao(),
                d.getCriadoEm()
        );
    }
}
