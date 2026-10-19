package br.edu.unibh.doesaude.service;

import br.edu.unibh.doesaude.domain.*;
import br.edu.unibh.doesaude.dto.DoacaoRequest;
import br.edu.unibh.doesaude.dto.DoacaoResponse;
import br.edu.unibh.doesaude.exception.NaoEncontradoException;
import br.edu.unibh.doesaude.exception.RegraNegocioException;
import br.edu.unibh.doesaude.repository.DoacaoRepository;
import br.edu.unibh.doesaude.repository.NecessidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;
    private final NecessidadeRepository necessidadeRepository;

    @Transactional
    public DoacaoResponse agendar(Usuario doador, DoacaoRequest request) {
        if (doador.getTipo() != TipoUsuario.DOADOR) {
            throw new RegraNegocioException("Somente doadores podem agendar uma doação");
        }

        Necessidade necessidade = necessidadeRepository.findById(request.necessidadeId())
                .orElseThrow(() -> new NaoEncontradoException("Necessidade não encontrada"));

        if (necessidade.getStatus() != StatusNecessidade.ABERTA) {
            throw new RegraNegocioException("Esta necessidade não está mais aberta para doações");
        }

        if (necessidade.getTipo() == TipoNecessidade.SANGUE && necessidade.getTipoSanguineo() != doador.getTipoSanguineo()) {
            throw new RegraNegocioException("Seu tipo sanguíneo não corresponde ao solicitado nesta necessidade");
        }

        Doacao doacao = Doacao.builder()
                .doador(doador)
                .necessidade(necessidade)
                .quantidade(request.quantidade())
                .dataAgendada(request.dataAgendada())
                .observacao(request.observacao())
                .build();

        return DoacaoResponse.de(doacaoRepository.save(doacao));
    }

    @Transactional(readOnly = true)
    public List<DoacaoResponse> listarPorDoador(Long doadorId) {
        return doacaoRepository.findByDoadorIdOrderByCriadoEmDesc(doadorId).stream()
                .map(DoacaoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DoacaoResponse> listarPorInstituicao(Long instituicaoId) {
        return doacaoRepository.findByNecessidadeInstituicaoIdOrderByCriadoEmDesc(instituicaoId).stream()
                .map(DoacaoResponse::de)
                .toList();
    }

    @Transactional
    public DoacaoResponse atualizarStatus(Long id, Usuario instituicao, StatusDoacao novoStatus) {
        if (novoStatus != StatusDoacao.RECEBIDA && novoStatus != StatusDoacao.NAO_RECEBIDA) {
            throw new RegraNegocioException("Status inválido para atualização");
        }

        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Doação não encontrada"));

        if (!doacao.getNecessidade().getInstituicao().getId().equals(instituicao.getId())) {
            throw new RegraNegocioException("Você só pode atualizar doações relacionadas à sua instituição");
        }

        if (doacao.getStatus() != StatusDoacao.AGENDADA) {
            throw new RegraNegocioException("Esta doação já foi avaliada e não pode mais ser alterada");
        }

        doacao.setStatus(novoStatus);

        if (novoStatus == StatusDoacao.RECEBIDA) {
            Necessidade necessidade = doacao.getNecessidade();
            necessidade.setQuantidadeAtendida(necessidade.getQuantidadeAtendida() + doacao.getQuantidade());
            if (necessidade.getQuantidadeAtendida() >= necessidade.getQuantidade()) {
                necessidade.setStatus(StatusNecessidade.ATENDIDA);
            }
        }

        return DoacaoResponse.de(doacao);
    }

    @Transactional
    public DoacaoResponse cancelarPorDoador(Long id, Usuario doador) {
        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Doação não encontrada"));

        if (!doacao.getDoador().getId().equals(doador.getId())) {
            throw new RegraNegocioException("Você só pode cancelar suas próprias doações");
        }

        if (doacao.getStatus() != StatusDoacao.AGENDADA) {
            throw new RegraNegocioException("Esta doação já foi avaliada pela instituição e não pode mais ser cancelada");
        }

        doacao.setStatus(StatusDoacao.CANCELADA);
        return DoacaoResponse.de(doacao);
    }
}
