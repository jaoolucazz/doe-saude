package br.edu.unibh.doesaude.repository;

import br.edu.unibh.doesaude.domain.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

    List<Doacao> findByDoadorIdOrderByCriadoEmDesc(Long doadorId);

    List<Doacao> findByNecessidadeInstituicaoIdOrderByCriadoEmDesc(Long instituicaoId);
}
