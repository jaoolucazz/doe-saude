package br.edu.unibh.doesaude.service;

import br.edu.unibh.doesaude.domain.*;
import br.edu.unibh.doesaude.dto.NecessidadeRequest;
import br.edu.unibh.doesaude.dto.NecessidadeResponse;
import br.edu.unibh.doesaude.exception.NaoEncontradoException;
import br.edu.unibh.doesaude.exception.RegraNegocioException;
import br.edu.unibh.doesaude.repository.NecessidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NecessidadeService {

    private final NecessidadeRepository necessidadeRepository;

    @Transactional
    public NecessidadeResponse criar(Usuario instituicao, NecessidadeRequest request) {
        if (instituicao.getTipo() != TipoUsuario.INSTITUICAO) {
            throw new RegraNegocioException("Somente instituições podem cadastrar necessidades");
        }
        if (request.tipo() == TipoNecessidade.SANGUE && request.tipoSanguineo() == null) {
            throw new RegraNegocioException("Informe o tipo sanguíneo necessário");
        }
        if ((request.tipo() == TipoNecessidade.MEDICAMENTO || request.tipo() == TipoNecessidade.OUTRO)
                && (request.nomeItem() == null || request.nomeItem().isBlank())) {
            throw new RegraNegocioException("Informe o nome do item necessário");
        }
        if (request.tipoBeneficiario() == TipoBeneficiario.PESSOA && request.nomeBeneficiario().isBlank()) {
            throw new RegraNegocioException("Informe o nome da pessoa beneficiada");
        }
        if (request.tipoBeneficiario() == TipoBeneficiario.GRUPO && request.nomeBeneficiario().isBlank()) {
            throw new RegraNegocioException("Informe o nome do grupo/instituição beneficiada");
        }

        Necessidade necessidade = Necessidade.builder()
                .instituicao(instituicao)
                .tipo(request.tipo())
                .descricao(request.descricao())
                .tipoSanguineo(request.tipoSanguineo())
                .nomeItem(request.nomeItem())
                .publicoAlvo(request.publicoAlvo())
                .tipoBeneficiario(request.tipoBeneficiario())
                .nomeBeneficiario(request.nomeBeneficiario())
                .descricaoBeneficiario(request.descricaoBeneficiario())
                .fotoBeneficiarioUrl(request.fotoBeneficiarioUrl())
                .quantidade(request.quantidade())
                .quantidadeAtendida(0)
                .urgencia(request.urgencia())
                .build();

        return NecessidadeResponse.de(necessidadeRepository.save(necessidade));
    }

    @Transactional(readOnly = true)
    public List<NecessidadeResponse> listar(TipoNecessidade tipo, TipoSanguineo tipoSanguineo, PublicoAlvo publicoAlvo, StatusNecessidade status) {
        return necessidadeRepository.buscarComFiltros(tipo, tipoSanguineo, publicoAlvo, status).stream()
                .map(NecessidadeResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NecessidadeResponse> listarPorInstituicao(Long instituicaoId) {
        return necessidadeRepository.findByInstituicaoIdOrderByCriadoEmDesc(instituicaoId).stream()
                .map(NecessidadeResponse::de)
                .toList();
    }

    public Necessidade buscarEntidadePorId(Long id) {
        return necessidadeRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Necessidade não encontrada"));
    }

    @Transactional(readOnly = true)
    public NecessidadeResponse buscarPorId(Long id) {
        return NecessidadeResponse.de(buscarEntidadePorId(id));
    }

    @Transactional
    public NecessidadeResponse cancelar(Long id, Usuario instituicao) {
        Necessidade necessidade = buscarEntidadePorId(id);
        if (!necessidade.getInstituicao().getId().equals(instituicao.getId())) {
            throw new RegraNegocioException("Você só pode cancelar necessidades cadastradas pela sua instituição");
        }
        necessidade.setStatus(StatusNecessidade.CANCELADA);
        return NecessidadeResponse.de(necessidade);
    }
}
